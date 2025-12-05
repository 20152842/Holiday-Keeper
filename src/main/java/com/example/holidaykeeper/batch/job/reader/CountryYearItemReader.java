package com.example.holidaykeeper.batch.job.reader;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.holidaykeeper.service.CountryService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CountryYearItemReader {
	private final CountryService countryService;

	/**
	 STEP 'recent5YearsSyncStep' 의 Reader
	 */
	@Bean
	@StepScope
	public ItemReader<CountryYear> recent5YearsReader() {
		List<Integer> years = IntStream.rangeClosed(2020, 2025).boxed().toList();
		List<String> codes = countryService.getAllCountryCodes(); // ["KR", "US", ...]

		List<CountryYear> items = years.stream()
			.flatMap(y -> codes.stream().map(c -> new CountryYear(y, c)))
			.toList();

		return new ListItemReader<>(items); // 간단한 in-memory reader
	}

	/**
	 STEP 'prevCurrYearSyncStep' 의 Reader
	 */
	@Bean
	@StepScope
	// @StepScope 덕분에 JobParameter(prevYear, currentYear) 를 런타임에 주입받을 수 있음
	/**
	 * @StepScope + @Value 사용 시 규칙
	 *
	 * 이런 메서드는 직접 호출하면 안 되고,
	 *
	 * @Bean 으로 등록 → Step 에서 빈을 주입받아 사용해야 함
	 *
	 * 그래야 jobParameters['prevYear'] 같은 값이 런타임에 올바르게 주입됨
	 */
	public ItemReader<CountryYear> prevAndCurrentYearReader(
		@Value("#{jobParameters['prevYear']}") Long prevYear,
		@Value("#{jobParameters['currentYear']}") Long currentYear
	) {
		List<Integer> years = List.of(prevYear.intValue(), currentYear.intValue());
		List<String> codes = countryService.getAllCountryCodes();

		List<CountryYear> items = years.stream()
			.flatMap(y -> codes.stream().map(c -> new CountryYear(y, c)))
			.toList();

		return new ListItemReader<>(items);
	}

	// DTO
	public record CountryYear(int year, String countryCode) {
	}
}
