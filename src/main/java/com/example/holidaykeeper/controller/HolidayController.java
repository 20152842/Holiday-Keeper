package com.example.holidaykeeper.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.holidaykeeper.dto.RefreshRequest;
import com.example.holidaykeeper.service.HolidayService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HolidayController {
	private final HolidayService holidayService;
	@PostMapping("/holidays")
	public ResponseEntity<?> loadAll() {
		Map<String, Object> res = holidayService.bulkLoadAllCountriesRecent5Years();
		return ResponseEntity.ok(res);
	}
	@GetMapping("/holidays")
	public ResponseEntity<?> search(){
		return null;
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
