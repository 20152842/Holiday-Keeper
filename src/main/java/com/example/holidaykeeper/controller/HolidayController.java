package com.example.holidaykeeper.controller;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.holidaykeeper.dto.HolidayDto;
import com.example.holidaykeeper.dto.PageResponseDto;
import com.example.holidaykeeper.dto.RefreshRequest;
import com.example.holidaykeeper.dto.ResponseDto;
import com.example.holidaykeeper.service.HolidayService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Holiday API", description = "공휴일 데이터 적재/조회/동기화/삭제 API")
public class HolidayController {
	private final HolidayService holidayService;
	@Operation(
		summary = "최근 5년 전체 공휴일 적재",
		description = "2020~2025년, Nager.Date가 제공하는 모든 국가의 공휴일을 한 번에 적재합니다."
	)
	@PostMapping("/holidays")
	public ResponseDto<?> loadAll() {
		return ResponseDto.success(HttpStatus.OK, "5 년 × N 개 국가를 일괄 적재",
			holidayService.bulkLoadAllCountriesRecent5Years());
	}
	@Operation(
		summary = "공휴일 검색",
		description = "연도, 국가코드, 날짜 범위(from~to), 타입(type) 기준으로 공휴일을 검색합니다. 결과는 페이징됩니다."
	)
	@GetMapping("/holidays")
	public PageResponseDto<?> search(
		@RequestParam Optional<Integer> year,
		@RequestParam Optional<String> country,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> from,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> to,
		@RequestParam Optional<String> type,
		@RequestParam Optional<Integer> page,
		@RequestParam Optional<Integer> size) {

		int p = page.orElse(0);
		int s = size.orElse(20);

		Pageable pageable = PageRequest.of(p, s, Sort.by("date").ascending());

		Page<HolidayDto> resultPage = holidayService.search(year, country, from, to, type, pageable);

		return resultPage.isEmpty() ? new PageResponseDto<>(HttpStatus.NOT_FOUND, "검색 실패", resultPage) :
									new PageResponseDto<>(HttpStatus.OK, "검색 완료", resultPage);
	}
	@Operation(
		summary = "특정 연도·국가 공휴일 재동기화",
		description = "지정한 연도·국가의 공휴일을 Nager.Date에서 다시 조회하여 기존 데이터를 삭제 후 재적재합니다."
	)
	@PutMapping("/holidays")
	public ResponseDto<?> refresh(@RequestBody RefreshRequest req) {
		return ResponseDto.success(HttpStatus.OK, "재동기화 완료",
			holidayService.refreshHoliday(req.getYear(), req.getCountry()));
	}
	@Operation(
		summary = "특정 연도·국가 공휴일 삭제",
		description = "지정한 연도·국가에 대한 공휴일 레코드를 DB에서 모두 삭제합니다."
	)
	@DeleteMapping("/holidays")
	public ResponseDto<?> delete(@RequestParam Integer year, @RequestParam String country) {
		return ResponseDto.success(HttpStatus.OK, "특정 연도·국가의 공휴일 레코드 전체 삭제",
			holidayService.deleteYearCountry(year, country));
	}

}
