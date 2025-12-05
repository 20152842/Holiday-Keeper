package com.example.holidaykeeper.batch.job.reader;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.example.holidaykeeper.service.CountryService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CountryYearItemReader {
	private final CountryService countryService;

	// DTO
	public record CountryYear(int year, String countryCode) {}

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
}
