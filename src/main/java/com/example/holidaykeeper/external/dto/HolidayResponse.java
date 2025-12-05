package com.example.holidaykeeper.external.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class HolidayResponse {
	public String date;
	public String localName;
	public String name;
	public boolean global;
	public boolean fixed;
	public List<String> counties;
	public List<String> types;

	public Boolean getGlobal() {
		return null;
	}
	public Boolean getFixed() {
		return null;
	}
}
