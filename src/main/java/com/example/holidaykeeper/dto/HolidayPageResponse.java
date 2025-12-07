package com.example.holidaykeeper.dto;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HolidayPageResponse {
	private int page;
	private int size;
	private long total;
	private List<HolidayDto> holidayDtosList;
}
