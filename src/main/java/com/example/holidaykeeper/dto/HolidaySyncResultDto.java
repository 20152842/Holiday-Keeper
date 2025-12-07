package com.example.holidaykeeper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HolidaySyncResultDto {

	@Schema(description = "동기화된 연도", example = "2025")
	private int year;

	@Schema(description = "국가 코드(ISO)", example = "KR")
	private String country;

	@Schema(description = "동기화된 공휴일 개수", example = "17")
	private int updatedCount;

	@Schema(description = "처리 상태", example = "success")
	private String status;
}