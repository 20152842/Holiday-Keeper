package com.example.holidaykeeper.batch.job.processor;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.holidaykeeper.batch.job.reader.CountryYearItemReader;
import com.example.holidaykeeper.dto.HolidayMapper;
import com.example.holidaykeeper.entity.Holiday;
import com.example.holidaykeeper.external.dto.HolidayResponse;
import com.example.holidaykeeper.external.service.ExternalNagerClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HolidaySyncProcessor implements
	ItemProcessor<CountryYearItemReader.CountryYear, HolidaySyncProcessor.CountryYearResult> {

	private final ExternalNagerClient externalNagerClient;

	/**
	 * Reader가 읽어온 원본 데이터를 → 비즈니스 로직에 맞게 가공/검증/필터링하는 역할
	 * CountryYearResult : redaer가 읽어온 result를 가공해서 나온 결과물
	 */
	@Override
	public CountryYearResult process(CountryYearItemReader.CountryYear item) {
		int year = item.year();
		String countryCode = item.countryCode();

		log.info("Fetching holidays from Nager API: year={}, country={}", year, countryCode);

		List<HolidayResponse> apiHolidays =
			externalNagerClient.getHolidaysByYearAndCountry(year, countryCode);

		List<Holiday> entities = apiHolidays.stream()
			.map(dto -> HolidayMapper.toEntity(dto, countryCode))
			.toList();

		return new CountryYearResult(year, countryCode, entities);
	}

	public record CountryYearResult(int year, String countryCode, List<Holiday> holidays) {}
}