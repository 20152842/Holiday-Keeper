package com.example.holidaykeeper.batch.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.holidaykeeper.entity.Country;
import com.example.holidaykeeper.service.CountryService;
import com.example.holidaykeeper.service.HolidayService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class HolidayScheduler {

	private final JobLauncher jobLauncher;
	private final Job prevAndCurrentYearSyncJob; // BatchConfig에서 정의할 Job

	@Scheduled(cron = "0 0 1 2 1 ?", zone = "Asia/Seoul")
	public void syncPrevAndCurrentYear() {
		int currentYear = LocalDate.now().getYear();
		int prevYear = currentYear - 1;

		log.info("Holiday batch trigger: prev={}, curr={}", prevYear, currentYear);

		try {
			JobParameters params = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis()) // 매번 다른 파라미터(필수)
				.addLong("prevYear", (long) prevYear)
				.addLong("currentYear", (long) currentYear)
				.toJobParameters();

			jobLauncher.run(prevAndCurrentYearSyncJob, params);
		} catch (Exception e) {
			log.error("Failed to launch prevAndCurrentYearSyncJob", e);
		}
	}
	/*
	 !!!스케줄러는 “언제”만, 실제 동기화는 Job!!!
	 아래의 코드는 스케줄러가 '실행 타이밍' + '비즈니스 로직' 두 개의 역할 수행
	 위의 코드는 두 개의 역할 분리
	 */

	// @Scheduled(cron = "0 0 1 2 *", zone = "Asia/Seoul")
	// public void syncPrevAndCurrentYear() {
	// 	int currentYear = LocalDate.now().getYear();
	// 	int prevYear = currentYear - 1;
	//
	// 	log.info("Holiday batch started for years: {} and {}", prevYear, currentYear);
	//
	// 	try {
	// 		List<Country> allCountries = countryService.getAllCountryCodes();
	//
	// 		for (Country country : allCountries) {
	// 			log.info("Sync start: country={}, years=[{}, {}]", country.getCode(), prevYear, currentYear);
	//
	// 			Map<String, Object> prevRes = holidayService.refreshHoliday(prevYear, country.getCode());
	// 			Map<String, Object> currRes = holidayService.refreshHoliday(currentYear, country.getCode());
	//
	// 			log.info("Result (prev): {} -> {}", country.getCode(), prevRes);
	// 			log.info("Result (curr): {} -> {}", country.getCode(), currRes);
	// 		}
	//
	// 		log.info("Holiday batch completed.");
	// 	} catch (Exception ex) {
	// 		log.error("Holiday batch failed: {}", ex.getMessage(), ex);
	// 	}
	// }
}