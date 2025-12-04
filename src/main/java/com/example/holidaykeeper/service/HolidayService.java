package com.example.holidaykeeper.service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.holidaykeeper.entity.Country;
import com.example.holidaykeeper.entity.Holiday;
import com.example.holidaykeeper.external.service.ExternalNagerClient;
import com.example.holidaykeeper.repository.CountryRepository;
import com.example.holidaykeeper.repository.HolidayRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HolidayService {

	private final CountryRepository countryRepo;
	private final HolidayRepository holidayRepo;

	private final List<Integer> TARGET_YEARS = List.of(2020,2021,2022,2023,2024,2025);
	@Transactional
	public Map<String, Object> bulkLoadAllCountriesRecent5Years() {
		List<ExternalNagerClient.CountryResponse> countries = nagerClient.getAvailableCountries();
		// save countries if not exist
		for (var c : countries) {
			countryRepo.findByCode(c.countryCode())
				.orElseGet(() -> countryRepo.save(Country.builder().code(c.countryCode()).name(c.name()).build()));
		}

		int totalInserted = 0;
		for (Integer year : TARGET_YEARS) {
			for (var c : countries) {
				var holidays = nagerClient.getHolidaysByYearAndCountry(year, c.countryCode());
				if (holidays == null) continue;
				List<Holiday> entities = holidays.stream().map(h -> {
					LocalDate date = LocalDate.parse(h.date());
					return Holiday.builder()
						.countryCode(c.countryCode())
						.date(date)
						.localName(h.localName())
						.name(h.name())
						.fixedFlag(h.fixed())
						.globalFlag(h.global())
						.type(String.join(",", Optional.ofNullable(h.types()).orElse(Collections.emptyList())))
						.counties(h.counties() == null ? null : String.join(",", h.counties()))
						.launchYear(year)
						.createdAt(OffsetDateTime.now())
						.build();
				}).collect(Collectors.toList());

				// Upsert approach: delete existing for that country+year then insert
				holidayRepo.deleteByCountryCodeAndLaunchYear(c.countryCode(), year);
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
}
