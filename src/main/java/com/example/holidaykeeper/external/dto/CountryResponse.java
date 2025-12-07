package com.example.holidaykeeper.external.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CountryResponse {
	//public vs private ??
	public String countryCode;
	public String name;
}
