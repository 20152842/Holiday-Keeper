package com.example.holidaykeeper.dto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class PageResponseDto<T> {

	private final int code;
	private final String status;
	private final String message;
	private final List<T> data;
	private final PaginationDto pagination;

	public PageResponseDto(HttpStatus httpStatus, Page<T> page, String message) {
		this.code = httpStatus.value();
		this.status = httpStatus.getReasonPhrase();
		this.message = message;
		this.data = page.getContent();
		this.pagination = new PaginationDto(page);
	}
}
