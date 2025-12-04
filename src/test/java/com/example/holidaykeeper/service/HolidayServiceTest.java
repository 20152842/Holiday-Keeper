package com.example.holidaykeeper.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.holidaykeeper.dto.HolidayMapper;
import com.example.holidaykeeper.external.service.ExternalNagerClient;
import com.example.holidaykeeper.repository.CountryRepository;
import com.example.holidaykeeper.repository.HolidayRepository;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

	@Mock
	private ExternalNagerClient holidayClient;
	@Mock
	private HolidayRepository holidayRepository;
	@Mock
	private CountryRepository countryRepository;
	@Mock
	private HolidayMapper holidayMapper;

	@InjectMocks
	private HolidayService holidayService;

	@Test
	void refreshHoliday_deletesAndInserts() {

	}
}