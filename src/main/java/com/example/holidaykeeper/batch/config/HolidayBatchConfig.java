package com.example.holidaykeeper.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestClientException;

import com.example.holidaykeeper.batch.job.listener.HolidayJobCompletionListener;
import com.example.holidaykeeper.batch.job.listener.HolidayStepCompletionListener;
import com.example.holidaykeeper.batch.job.processor.HolidaySyncProcessor;
import com.example.holidaykeeper.batch.job.reader.CountryYearItemReader;
import com.example.holidaykeeper.batch.job.tasklet.SyncSummaryTasklet;
import com.example.holidaykeeper.batch.job.writer.HolidaySyncWriter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class HolidayBatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	private final HolidayJobCompletionListener jobCompletionListener;
	private final HolidayStepCompletionListener stepCompletionListener;

	private final HolidaySyncProcessor holidaySyncProcessor;
	private final HolidaySyncWriter holidaySyncWriter;
	private final SyncSummaryTasklet syncSummaryTasklet;

	/** Step 은 @Bean으로 등록
	 * Job 메서드는 그 Step 을 파라미터로 주입받아서 사용
	 */

	// 1) 2020~2025 전체 적재 Job (수동 실행용)
	@Bean
	public Job recent5YearsSyncJob(
		@Qualifier("recent5YearsSyncStep") Step recent5YearsSyncStep,
		@Qualifier("syncSummaryStep") Step syncSummaryStep
	) {
		return new JobBuilder("recent5YearsSyncJob", jobRepository)
			.listener(jobCompletionListener)
			.start(recent5YearsSyncStep)
			.next(syncSummaryStep)
			.build();
	}

	// 2) 전년도 + 금년도 Sync Job (스케줄러용)
	@Bean
	public Job prevAndCurrentYearSyncJob(
		@Qualifier("prevCurrYearSyncStep") Step prevCurrYearSyncStep,
		@Qualifier("syncSummaryStep") Step syncSummaryStep
	) {
		return new JobBuilder("prevAndCurrentYearSyncJob", jobRepository)
			.listener(jobCompletionListener)
			.start(prevCurrYearSyncStep)
			.next(syncSummaryStep)
			.build();
	}

	@Bean
	public Step syncSummaryStep() {
		return new StepBuilder("syncSummaryStep", jobRepository)
			.tasklet(syncSummaryTasklet, transactionManager)
			.build();
	}

	/**
	 Step -> “(year, countryCode) 조합”을 Chunk 단위로 처리

	 CountryYear : (year, countryCode) 한 건
	 CountryYearResult : 해당 조합의 처리 결과 (ex : insert 건수 등) + List<Holiday>

	 Chunk 단위: “5개 CountryYear마다 한 번 커밋”
	 → 한 번에 5년×N국가를 다 처리하지 않고, Chunk별로 안정적으로 커밋.
	 */
	@Bean
	public Step recent5YearsSyncStep(
		@Qualifier("recent5YearsReader")
		ItemReader<CountryYearItemReader.CountryYear> recent5YearsReader) {
		return new StepBuilder("recent5YearsSyncStep", jobRepository)
			.<CountryYearItemReader.CountryYear, HolidaySyncProcessor.CountryYearResult>chunk(5, transactionManager)
			.reader(recent5YearsReader)
			.processor(holidaySyncProcessor)
			.writer(holidaySyncWriter)
			.faultTolerant()
			.retry(RestClientException.class)     // 외부 API 통신 오류 재시도
			.retryLimit(3)
			.skip(IllegalStateException.class)    // 특정 국가/연도 실패 시 건너뛰기
			.skipLimit(10)
			.listener(stepCompletionListener)
			.build();
	}

	@Bean
	public Step prevCurrYearSyncStep(
		@Qualifier("prevAndCurrentYearReader")
		ItemReader<CountryYearItemReader.CountryYear> prevAndCurrentYearReader) {
		return new StepBuilder("prevCurrYearSyncStep", jobRepository)
			.<CountryYearItemReader.CountryYear, HolidaySyncProcessor.CountryYearResult>chunk(5, transactionManager)
			.reader(prevAndCurrentYearReader)
			.processor(holidaySyncProcessor)
			.writer(holidaySyncWriter)
			.faultTolerant()
			.retry(RestClientException.class)
			.retryLimit(3)
			.skip(IllegalStateException.class)
			.skipLimit(10)
			.listener(stepCompletionListener)
			.build();
	}
}
