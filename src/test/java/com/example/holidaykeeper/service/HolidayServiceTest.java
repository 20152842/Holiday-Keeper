package com.example.holidaykeeper.service;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.example.holidaykeeper.dto.HolidayDto;
import com.example.holidaykeeper.entity.Holiday;
import com.example.holidaykeeper.external.service.ExternalNagerClient;
import com.example.holidaykeeper.repository.HolidayRepository;

@SpringBootTest
@Transactional
class HolidayServiceTest {

	@Autowired
	private HolidayService holidayService;

	@Autowired
	private HolidayRepository holidayRepository;

	// 외부 API 호출 막기 위한 Mock
	@Mock
	private ExternalNagerClient externalNagerClient;

	@BeforeEach
	void setUp() {
		// H2에 테스트용 Holiday 데이터 세팅
		// KR 2025-01-01 Public
		holidayRepository.save(
			Holiday.builder()
				.countryCode("KR")
				.date(LocalDate.of(2025, 1, 1))
				.localName("신정")
				.name("New Year's Day")
				.fixed(true)
				.global(true)
				.type("Public")
				.launchYear(2025)
				.createdAt(OffsetDateTime.now())
				.build()
		);

		// KR 2025-02-10 Bank
		holidayRepository.save(
			Holiday.builder()
				.countryCode("KR")
				.date(LocalDate.of(2025, 2, 10))
				.localName("은행 휴무일")
				.name("Bank Holiday")
				.fixed(false)
				.global(false)
				.type("Bank")
				.launchYear(2025)
				.createdAt(OffsetDateTime.now())
				.build()
		);

		// US 2025-01-01 Public
		holidayRepository.save(
			Holiday.builder()
				.countryCode("US")
				.date(LocalDate.of(2025, 1, 1))
				.localName("New Year's Day")
				.name("New Year's Day")
				.fixed(true)
				.global(true)
				.type("Public")
				.launchYear(2025)
				.createdAt(OffsetDateTime.now())
				.build()
		);
	}

	@Test
	@DisplayName("국가 + 연도 기준 검색이 정상 동작한다 (KR, 2025)")
	void search_byCountryAndYear() {
		// given
		var pageable = PageRequest.of(0, 10);

		// when
		Page<HolidayDto> result = holidayService.search(
			Optional.of(2025),
			Optional.of("KR"),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			pageable
		);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent())
			.extracting(HolidayDto::getCountryCode)
			.containsOnly("KR");
	}

	@Test
	@DisplayName("from~to 날짜 범위 검색이 정상 동작한다")
	void search_byDateRange() {
		// given
		var pageable = PageRequest.of(0, 10);

		// 2025-01-01 ~ 2025-01-31 사이 (KR, US 하나씩)
		// when
		Page<HolidayDto> result = holidayService.search(
			Optional.empty(),
			Optional.empty(),
			Optional.of(LocalDate.of(2025, 1, 1)),
			Optional.of(LocalDate.of(2025, 1, 31)),
			Optional.empty(),
			pageable
		);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2); // KR, US
		assertThat(result.getContent())
			.extracting(HolidayDto::getDate)
			.containsOnly(LocalDate.of(2025, 1, 1));
	}

	@Test
	@DisplayName("국가 + 타입 필터가 함께 적용된다 (KR + type=Public)")
	void search_byCountryAndType() {
		// given
		var pageable = PageRequest.of(0, 10);

		// when
		Page<HolidayDto> result = holidayService.search(
			Optional.empty(),
			Optional.of("KR"),
			Optional.empty(),
			Optional.empty(),
			Optional.of("Public"),
			pageable
		);

		// then
		assertThat(result.getTotalElements()).isEqualTo(1);
		HolidayDto dto = result.getContent().get(0);

		assertThat(dto.getCountryCode()).isEqualTo("KR");
		assertThat(dto.getTypes()).contains("Public");
		assertThat(dto.getDate()).isEqualTo(LocalDate.of(2025, 1, 1));
	}
}