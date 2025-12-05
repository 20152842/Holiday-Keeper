package com.example.holidaykeeper.scheduler;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

	private final HolidayService holidayService;
	private final CountryService countryService;

	@Scheduled(cron = "0 0 1 2 *", zone = "Asia/Seoul")
	public void syncPrevAndCurrentYear() {
		int currentYear = LocalDate.now().getYear();
		int prevYear = currentYear - 1;

		log.info("Holiday batch started for years: {} and {}", prevYear, currentYear);

		try {
			List<Country> allCountries = countryService.getAllCountryCodes();

			for (Country country : allCountries) {
				log.info("Sync start: country={}, years=[{}, {}]", country.getCode(), prevYear, currentYear);

				Map<String, Object> prevRes = holidayService.refreshHoliday(prevYear, country.getCode());
				Map<String, Object> currRes = holidayService.refreshHoliday(currentYear, country.getCode());

				log.info("Result (prev): {} -> {}", country.getCode(), prevRes);
				log.info("Result (curr): {} -> {}", country.getCode(), currRes);
			}

			log.info("Holiday batch completed.");
		} catch (Exception ex) {
			log.error("Holiday batch failed: {}", ex.getMessage(), ex);
		}
	}
}