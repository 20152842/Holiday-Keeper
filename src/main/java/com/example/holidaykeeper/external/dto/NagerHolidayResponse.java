package com.example.holidaykeeper.external.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NagerHolidayResponse {
	private String date;
	private String localName;
	private String name;
	private boolean global;
	private boolean fixed;
	private List<String> counties;
	private List<String> types;
}
