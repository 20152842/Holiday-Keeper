package com.example.holidaykeeper.dto;

import java.time.LocalDate;

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
}
