package com.example.holidaykeeper.repository;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.holidaykeeper.entity.Holiday;

public interface HolidayRepositoryCustom {
	Page<Holiday> search(
		Integer launchYear,
		String countryCode,
		LocalDate from,
		LocalDate to,
		String type,
		Pageable pageable
	);
}
