package com.example.holidaykeeper.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.holidaykeeper.dto.HolidayDto;
import com.example.holidaykeeper.entity.Country;
import com.example.holidaykeeper.entity.Holiday;
import com.example.holidaykeeper.external.dto.CountryResponse;
import com.example.holidaykeeper.external.dto.HolidayResponse;
import com.example.holidaykeeper.external.service.ExternalNagerClient;
import com.example.holidaykeeper.repository.CountryRepository;
import com.example.holidaykeeper.repository.HolidayRepository;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

	@Mock
	private ExternalNagerClient nagerClient;

	@Mock
	private CountryRepository countryRepo;

	@Mock
	private HolidayRepository holidayRepo;

	@InjectMocks
	private HolidayService holidayService;

	@Test
	@DisplayName("bulkLoadAllCountriesRecent5Years - 최근 5년 × N개 국가를 일괄 적재하고 결과를 반환한다")
	void bulkLoadAllCountriesRecent5Years_success() {
		// given
		CountryResponse kr = mock(CountryResponse.class);
		given(kr.getCountryCode()).willReturn("KR");
		given(kr.getName()).willReturn("Korea");

		CountryResponse us = mock(CountryResponse.class);
		given(us.getCountryCode()).willReturn("US");
		given(us.getName()).willReturn("United States");

		List<CountryResponse> countryResponses = List.of(kr, us);
		given(nagerClient.getAvailableCountries()).willReturn(countryResponses);

		given(countryRepo.findByCountryCode(anyString()))
			.willReturn(Optional.empty());

		HolidayResponse h1 = mock(HolidayResponse.class);
		given(h1.getDate()).willReturn("2020-01-01");
		given(h1.getLocalName()).willReturn("신정");
		given(h1.getName()).willReturn("New Year");
		given(h1.isFixed()).willReturn(true);
		given(h1.isGlobal()).willReturn(true);
		given(h1.getTypes()).willReturn(List.of("Public"));
		given(h1.getCounties()).willReturn(null);

		List<HolidayResponse> holidayResponses = List.of(h1);

		given(nagerClient.getHolidaysByYearAndCountry(anyInt(), anyString()))
			.willReturn(holidayResponses);

		given(holidayRepo.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

		// when
		Map<String, Object> result = holidayService.bulkLoadAllCountriesRecent5Years();

		// then
		// 결과 Map 검증
		assertThat(result.get("status")).isEqualTo("success");
		assertThat(result.get("years")).isInstanceOf(List.class);
		assertThat(result.get("countriesCount")).isEqualTo(2); // KR, US

		int totalInserted = (int)result.get("totalHolidaysInserted");
		// 2020~2025 6년 × 2개 국가 × 1개 holiday = 12
		assertThat(totalInserted).isEqualTo(6 * 2 * 1);

		// Country 저장이 국가 수만큼 호출되었는지
		then(countryRepo).should(times(2)).save(any(Country.class));

		// delete + saveAll 이 몇 번 호출되었는지 (6년 × 2국가 = 12번)
		then(holidayRepo).should(times(12))
			.deleteByCountryCodeAndLaunchYear(anyString(), anyInt());
		then(holidayRepo).should(times(12))
			.saveAll(anyList());
	}

	@Test
	@DisplayName("search - QueryDSL 기반 검색 결과를 DTO로 매핑해 반환한다")
	void search_usesQuerydslRepositoryAndMapsToDto() {
		// given
		Integer launchYear = 2025;
		String country = "KR";
		LocalDate from = LocalDate.of(2025, 1, 1);
		LocalDate to = LocalDate.of(2025, 12, 31);
		String type = "Public";
		Pageable pageable = PageRequest.of(0, 10);

		Holiday entity = Holiday.builder()
			.id(1L)
			.countryCode("KR")
			.date(LocalDate.of(2025, 1, 1))
			.localName("신정")
			.name("New Year")
			.fixed(true)
			.global(true)
			.type("Public")
			.counties(null)
			.launchYear(2025)
			.createdAt(OffsetDateTime.now())
			.build();

		Page<Holiday> entityPage =
			new PageImpl<>(List.of(entity), pageable, 1);

		// HolidayRepositoryImpl.search(...) 호출 시 위의 페이지를 반환
		given(holidayRepo.search(
			launchYear,
			country,
			from,
			to,
			type,
			pageable
		)).willReturn(entityPage);

		// when
		Page<HolidayDto> result = holidayService.search(
			Optional.of(launchYear),
			Optional.of(country),
			Optional.of(from),
			Optional.of(to),
			Optional.of(type),
			pageable
		);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		HolidayDto dto = result.getContent().get(0);
		assertThat(dto.getCountryCode()).isEqualTo("KR");
		assertThat(dto.getDate()).isEqualTo(LocalDate.of(2025, 1, 1));
		assertThat(dto.getName()).isEqualTo("New Year");
		assertThat(dto.getLocalName()).isEqualTo("신정");

		// repositoryImpl.search(...) 가 정확한 파라미터로 호출됐는지 검증
		then(holidayRepo).should().search(
			launchYear,
			country,
			from,
			to,
			type,
			pageable
		);
	}

	@Test
	@DisplayName("refreshHoliday - 외부 API에서 데이터가 있으면 기존 데이터 삭제 후 재저장한다")
	void refreshHoliday_success() {
		// given
		int year = 2025;
		String countryCode = "KR";

		HolidayResponse h1 = mock(HolidayResponse.class);
		given(h1.getDate()).willReturn("2025-01-01");
		given(h1.getLocalName()).willReturn("신정");
		given(h1.getName()).willReturn("New Year");
		given(h1.isFixed()).willReturn(true);
		given(h1.isGlobal()).willReturn(true);
		given(h1.getTypes()).willReturn(List.of("Public"));
		given(h1.getCounties()).willReturn(null);

		List<HolidayResponse> holidays = List.of(h1);

		given(nagerClient.getHolidaysByYearAndCountry(year, countryCode))
			.willReturn(holidays);

		given(holidayRepo.saveAll(anyList()))
			.willAnswer(invocation -> invocation.getArgument(0));

		ArgumentCaptor<List<Holiday>> captor = ArgumentCaptor.forClass(List.class);

		// when
		Map<String, Object> result = holidayService.refreshHoliday(year, countryCode);

		// then
		assertThat(result.get("status")).isEqualTo("success");
		assertThat(result.get("years")).isEqualTo(year);
		assertThat(result.get("country")).isEqualTo(countryCode);
		assertThat(result.get("updatedCount")).isEqualTo(1);

		// 기존 데이터 삭제 후 저장 호출 검증
		then(holidayRepo).should()
			.deleteByCountryCodeAndLaunchYear(countryCode, year);
		then(holidayRepo).should()
			.saveAll(captor.capture());

		List<Holiday> saved = captor.getValue();
		assertThat(saved).hasSize(1);
		Holiday savedHoliday = saved.get(0);
		assertThat(savedHoliday.getCountryCode()).isEqualTo(countryCode);
		assertThat(savedHoliday.getDate()).isEqualTo(LocalDate.of(2025, 1, 1));
		assertThat(savedHoliday.getName()).isEqualTo("New Year");
	}

	@Test
	@DisplayName("refreshHoliday - 외부 API가 null을 반환하면 에러 상태를 반환하고 DB 변경은 하지 않는다")
	void refreshHoliday_noData() {
		// given
		int year = 2025;
		String countryCode = "KR";

		given(nagerClient.getHolidaysByYearAndCountry(year, countryCode))
			.willReturn(null);

		// when
		Map<String, Object> result = holidayService.refreshHoliday(year, countryCode);

		// then
		assertThat(result.get("status")).isEqualTo("error");
		assertThat(result.get("message")).isEqualTo("no data");

		// delete/saveAll 이 호출되지 않았는지 검증
		then(holidayRepo).should(never())
			.deleteByCountryCodeAndLaunchYear(anyString(), anyInt());
		then(holidayRepo).should(never())
			.saveAll(anyList());
	}

	@Test
	@DisplayName("deleteYearCountry - 특정 연도·국가의 데이터를 삭제하고 결과맵을 반환한다")
	void deleteYearCountry_success() {
		// given
		int year = 2020;
		String countryCode = "US";

		// deleteByCountryCodeAndLaunchYear 는 void 이므로 따로 given 필요 X

		// when
		Map<String, Object> result = holidayService.deleteYearCountry(year, countryCode);

		// then
		assertThat(result.get("status")).isEqualTo("success");
		assertThat(result.get("deleted country")).isEqualTo(countryCode);
		assertThat(result.get("deleted year")).isEqualTo(year);

		then(holidayRepo).should()
			.deleteByCountryCodeAndLaunchYear(countryCode, year);
	}
}