package com.example.holidaykeeper.dto;

import java.time.LocalDate;

import com.example.holidaykeeper.entity.Holiday;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayDto {
	private String countryCode;
	private LocalDate date;
	private String localName;
	private String name;
	private Boolean fixed;
	private Boolean global;
	private String[] types;
	private String counties;
	private Integer launchYear;
	private java.time.OffsetDateTime createdAt;

	public static HolidayDto fromEntity(Holiday h) {
		return HolidayDto.builder()
			.countryCode(h.getCountryCode())
			.date(h.getDate())
			.localName(h.getLocalName())
			.name(h.getName())
			.global(h.getGlobal())
			.fixed(h.getFixed())
			.launchYear(h.getLaunchYear())
			.createdAt(h.getCreatedAt())
			.types(h.getType() == null ? new String[]{} : h.getType().split(","))
			.counties(h.getCounties())
			.build();
	}
}
