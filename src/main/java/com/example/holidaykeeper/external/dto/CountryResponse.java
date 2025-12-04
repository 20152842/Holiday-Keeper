package com.example.holidaykeeper.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryResponse {
	private String countryCode;
	private String name;
}
