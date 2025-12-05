package com.example.holidaykeeper.controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.holidaykeeper.dto.HolidayDto;
import com.example.holidaykeeper.dto.HolidayPageResponse;
import com.example.holidaykeeper.dto.PageResponseDto;
import com.example.holidaykeeper.dto.RefreshRequest;
import com.example.holidaykeeper.dto.ResponseDto;
import com.example.holidaykeeper.service.HolidayService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HolidayController {
	private final HolidayService holidayService;
	@PostMapping("/holidays")
	public ResponseDto<?> loadAll() {
		return ResponseDto.success(HttpStatus.OK, "5 년 × N 개 국가를 일괄 적재",
			holidayService.bulkLoadAllCountriesRecent5Years());
	}
	@GetMapping("/holidays")
	public PageResponseDto<?> search(
		@RequestParam Optional<Integer> year,
		@RequestParam Optional<String> country,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> from,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> to,
		@RequestParam Optional<Integer> page,
		@RequestParam Optional<Integer> size){

		int p = page.orElse(0);
		int s = size.orElse(20);

		Pageable pageable = PageRequest.of(p, s, Sort.by("date").ascending());

		Page<HolidayDto> resultPage = holidayService.search(year, country, from, to, pageable);

		return new PageResponseDto<>(HttpStatus.OK, "검색 완료", resultPage);
	}

	@PutMapping("/holidays")
	public ResponseEntity<?> refresh(@RequestBody RefreshRequest req) {
		Map<String, Object> res = holidayService.refreshYearCountry(req.getYear(), req.getCountry());
		return ResponseEntity.ok(res);
	}

	@DeleteMapping("/holidays")
	public ResponseEntity<?> delete(@RequestParam Integer year, @RequestParam String country) {
		Map<String, Object> res = holidayService.deleteYearCountry(year, country);
		return ResponseEntity.ok(res);
	}

}
