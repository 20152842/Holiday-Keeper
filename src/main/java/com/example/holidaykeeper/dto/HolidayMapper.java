package com.example.holidaykeeper.dto;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.example.holidaykeeper.entity.Holiday;
import com.example.holidaykeeper.external.dto.HolidayResponse;

@Component
public class HolidayMapper {
	public static Holiday toEntity(HolidayResponse dto, String countryCode) {
		return Holiday.builder()
			.countryCode(countryCode)
			.date(LocalDate.parse(dto.getDate()))
			.localName(dto.getLocalName())
			.name(dto.getName())
			.fixed(dto.isFixed())
			.global(dto.isGlobal())
			.type(String.join(",", dto.getTypes()))
			.counties(dto.getCounties() != null ? String.join(",", dto.getCounties()) : null)
			.launchYear(LocalDate.parse(dto.getDate()).getYear())
			.build();
	}
}
