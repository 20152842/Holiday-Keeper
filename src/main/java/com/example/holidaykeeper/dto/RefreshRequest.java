package com.example.holidaykeeper.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshRequest {
	private Integer year;
	private String country;
}