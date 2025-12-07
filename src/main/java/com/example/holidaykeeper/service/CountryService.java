package com.example.holidaykeeper.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.holidaykeeper.entity.Country;
import com.example.holidaykeeper.repository.CountryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryService {

	private final CountryRepository countryRepository;

	public List<String> getAllCountryCodes() {
		return countryRepository.findAll().stream()
			.map(Country::getCountryCode)
			.toList();
	}
}