package com.example.holidaykeeper.repository;

import com.example.holidaykeeper.entity.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom  {
	// 결과는 페이징 형태로 응답

	// 연도별·국가별 필터 기반 공휴일 조회
	Page<Holiday> findByCountryCodeAndLaunchYear(String countryCode, Integer launchYear, Pageable pageable);
	Page<Holiday> findByCountryCode(String countryCode, Pageable pageable);

	// from ~ to 기간, 공휴일 타입 등 추가 필터 자유 확장
	Page<Holiday> findByDateBetween(LocalDate from, LocalDate to, Pageable pageable);
	List<Holiday> findByCountryCodeAndDateBetween(String countryCode, LocalDate from, LocalDate to);
	List<Holiday> findByCountryCodeAndLaunchYear(String countryCode, Integer launchYear);

	// 특정 연도·국가의 공휴일 레코드 전체 삭제
	void deleteByCountryCodeAndLaunchYear(String countryCode, Integer launchYear);
	void deleteByCountryCodeAndDateBetween(String countryCode, LocalDate from, LocalDate to);

	boolean existsByCountryCodeAndDate(String countryCode, LocalDate date);

}
