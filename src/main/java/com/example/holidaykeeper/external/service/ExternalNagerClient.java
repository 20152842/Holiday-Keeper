package com.example.holidaykeeper.external.service;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.holidaykeeper.external.dto.CountryResponse;
import com.example.holidaykeeper.external.dto.HolidayResponse;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class ExternalNagerClient {

	private final WebClient nagerWebClient;


	// 국가 목록
	public List<CountryResponse> getAvailableCountries() {
		return nagerWebClient.get()
			.uri("/AvailableCountries")
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<List<CountryResponse>>() {})
			.block();
		// 동기처리
	}

	// 특정 연도 공휴일
	public List<HolidayResponse> getHolidaysByYearAndCountry(int year, String countryCode) {
		return nagerWebClient.get()
			.uri("/PublicHolidays/{year}/{countryCode}", year, countryCode)
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<List<HolidayResponse>>() {})
			.block();
	}


}
