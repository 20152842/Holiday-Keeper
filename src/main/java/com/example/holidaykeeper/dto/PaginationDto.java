package com.example.holidaykeeper.dto;

import org.springframework.data.domain.Page;

import lombok.Getter;

@Getter
public class PaginationDto {
	private int page;
	private int size;
	private int totalPages;
	private long totalElements;

	public PaginationDto(Page page) {
		this.page = page.getNumber();
		this.size = page.getSize();
		this.totalPages = page.getTotalPages();
		this.totalElements = page.getTotalElements();
	}
}
