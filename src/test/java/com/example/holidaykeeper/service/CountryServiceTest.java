package com.example.holidaykeeper.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.holidaykeeper.entity.Country;
import com.example.holidaykeeper.repository.CountryRepository;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

	@Mock
	private CountryRepository countryRepository;

	@InjectMocks
	private CountryService countryService;

	@Test
	@DisplayName("모든 Country 엔티티에서 countryCode만 뽑아서 리스트로 반환한다")
	void getAllCountryCodes_returnsOnlyCodes() {
		// given
		Country kr = Country.builder()
			.id(1L)
			.countryCode("KR")
			.name("Korea")
			.build();

		Country us = Country.builder()
			.id(2L)
			.countryCode("US")
			.name("United States")
			.build();

		given(countryRepository.findAll()).willReturn(List.of(kr, us));

		// when
		List<String> codes = countryService.getAllCountryCodes();

		// then
		assertThat(codes)
			.containsExactlyInAnyOrder("KR", "US");
	}

	@Test
	@DisplayName("국가가 없으면 빈 리스트를 반환한다")
	void getAllCountryCodes_empty() {
		// given
		given(countryRepository.findAll()).willReturn(List.of());

		// when
		List<String> codes = countryService.getAllCountryCodes();

		// then
		assertThat(codes).isEmpty();
	}
}