package com.example.holidaykeeper.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.holidaykeeper.dto.HolidayDto;
import com.example.holidaykeeper.dto.HolidaySyncResultDto;
import com.example.holidaykeeper.entity.Country;
import com.example.holidaykeeper.entity.Holiday;
import com.example.holidaykeeper.external.dto.CountryResponse;
import com.example.holidaykeeper.external.dto.HolidayResponse;
import com.example.holidaykeeper.external.service.ExternalNagerClient;
import com.example.holidaykeeper.repository.CountryRepository;
import com.example.holidaykeeper.repository.HolidayRepository;
import com.example.holidaykeeper.repository.HolidayRepositoryImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HolidayService {
	private final ExternalNagerClient nagerClient;
	private final CountryRepository countryRepo;
	private final HolidayRepository holidayRepo;
	private final List<Integer> TARGET_YEARS = List.of(2020,2021,2022,2023,2024,2025);
	@Transactional
	public Map<String, Object> bulkLoadAllCountriesRecent5Years() {
		List<CountryResponse> countries = nagerClient.getAvailableCountries();
		for (CountryResponse c : countries) {
			countryRepo.findByCountryCode(c.getCountryCode())
				.orElseGet(() -> countryRepo.save(Country.builder()
																	.countryCode(c.getCountryCode())
																	.name(c.getName())
																	.build()));
		}

		int totalInserted = 0;
		for (Integer year : TARGET_YEARS) {
			for (CountryResponse c : countries) {
				List<HolidayResponse> holidays = nagerClient.getHolidaysByYearAndCountry(year, c.getCountryCode());
				if (holidays == null) continue;
				List<Holiday> entities = holidays.stream().map(h -> {
					return Holiday.builder()
						.countryCode(c.getCountryCode())
						.date(LocalDate.parse(h.getDate()))
						.localName(h.getLocalName())
						.name(h.getName())
						.fixed(h.isFixed())
						.global(h.isGlobal())
						.type(String.join(",", Optional.ofNullable(h.getTypes()).orElse(Collections.emptyList())))
						.counties(h.getCounties() == null ? null : String.join(",", h.getCounties()))
						.launchYear(year)
						.createdAt(OffsetDateTime.now())
						.build();
				}).collect(Collectors.toList());

				holidayRepo.deleteByCountryCodeAndLaunchYear(c.getCountryCode(), year);
				holidayRepo.saveAll(entities);
				totalInserted += entities.size();
			}
		}

		Map<String,Object> result = new HashMap<>();
		result.put("status","success");
		result.put("years", TARGET_YEARS);
		result.put("countriesCount", countries.size());
		result.put("totalHolidaysInserted", totalInserted);
		return result;
	}

	/**
	 * JPA Version
	 */
	// public Page<HolidayDto> search(Optional<Integer> year, Optional<String> country,
	// 								Optional<LocalDate> from, Optional<LocalDate> to, Pageable pageable){
	// 	// 날짜 기간 검색
	// 	if (from.isPresent() && to.isPresent()) {
	// 		return holidayRepo.findByDateBetween(from.get(), to.get(), pageable)
	// 			.map(this::toDto);
	// 	}
	//
	// 	// 국가 + 연도 검색
	// 	if (country.isPresent() && year.isPresent()) {
	// 		return holidayRepo.findByCountryCodeAndLaunchYear(
	// 			country.get(),
	// 			year.get(),
	// 			pageable
	// 		).map(this::toDto);
	// 	}
	//
	// 	// 국가만 검색
	// 	if (country.isPresent()) {
	// 		return holidayRepo.findByCountryCode(country.get(), pageable)
	// 			.map(this::toDto);
	// 	}
	//
	// 	// 기본 전체 조회
	// 	return holidayRepo.findAll(pageable).map(this::toDto);
	// }

	/**
	 * QueryDSL Version
	 */
	public Page<HolidayDto> search(
		Optional<Integer> launchYear,
		Optional<String> country,
		Optional<LocalDate> from,
		Optional<LocalDate> to,
		Optional<String> type,
		Pageable pageable) {

		Integer yearVal = launchYear.orElse(null);
		String countryVal = country.orElse(null);
		LocalDate fromVal = from.orElse(null);
		LocalDate toVal = to.orElse(null);
		String typeVal = type.orElse(null);

		Page<Holiday> page = holidayRepo.search(
			yearVal,
			countryVal,
			fromVal,
			toVal,
			typeVal,
			pageable
		);

		return page.map(this::toDto);
	}


	@Transactional
	public HolidaySyncResultDto refreshHoliday(int year, String countryCode) {
		List<HolidayResponse> holidays = nagerClient.getHolidaysByYearAndCountry(year, countryCode);
		if (holidays == null) {
			throw new IllegalStateException("No data from API");
		}
		holidayRepo.deleteByCountryCodeAndLaunchYear(countryCode, year);

		List<Holiday> entities = holidays.stream().map(h -> {
			return Holiday.builder()
				.countryCode(countryCode)
				.date(LocalDate.parse(h.getDate()))
				.localName(h.getLocalName())
				.name(h.getName())
				.fixed(h.isFixed())
				.global(h.isGlobal())
				.type(String.join(",", Optional.ofNullable(h.getTypes()).orElse(Collections.emptyList())))
				.counties(h.getCounties() == null ? null : String.join(",", h.getCounties()))
				.launchYear(year)
				.createdAt(OffsetDateTime.now())
				.build();
		}).collect(Collectors.toList());

		holidayRepo.saveAll(entities);

		return HolidaySyncResultDto.builder()
			.year(year)
			.country(countryCode)
			.updatedCount(entities.size())
			.status("success")
			.build();
	}

	@Transactional
	public Map<String,Object> deleteYearCountry(int year, String countryCode) {
		holidayRepo.deleteByCountryCodeAndLaunchYear(countryCode, year);
		Map<String,Object> result = new HashMap<>();
		result.put("status","success");
		result.put("deleted country", countryCode);
		result.put("deleted year", year);
		return result;
	}

	private HolidayDto toDto(Holiday h) {
		return HolidayDto.fromEntity(h);
	}

}
