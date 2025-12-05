package com.example.holidaykeeper.batch.job.writer;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.example.holidaykeeper.batch.job.processor.HolidaySyncProcessor;
import com.example.holidaykeeper.entity.Holiday;
import com.example.holidaykeeper.repository.HolidayRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HolidaySyncWriter implements ItemWriter<HolidaySyncProcessor.CountryYearResult> {

	private final HolidayRepository holidayRepository;

	@Override
	public void write(Chunk<? extends HolidaySyncProcessor.CountryYearResult> chunk) {
		for (HolidaySyncProcessor.CountryYearResult result : chunk) {
			String countryCode = result.countryCode();
			int year = result.year();
			List<Holiday> holidays = result.holidays();

			log.info("Upserting holidays: year={}, country={}, count={}", year, countryCode, holidays.size());

			// 1) 기존 데이터 삭제
			holidayRepository.deleteByCountryCodeAndLaunchYear(countryCode, year);

			// 2) 새 데이터 저장
			holidayRepository.saveAll(holidays);
		}
	}
}