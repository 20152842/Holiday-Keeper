package com.example.holidaykeeper.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.holidaykeeper.entity.Holiday;
import com.example.holidaykeeper.entity.QHoliday;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<Holiday> search(
		Integer year,
		String countryCode,
		LocalDate from,
		LocalDate to,
		String type,
		Pageable pageable
	) {
		QHoliday h = QHoliday.holiday;

		BooleanBuilder builder = new BooleanBuilder();

		if (year != null) {
			builder.and(h.launchYear.eq(year));
		}

		if (countryCode != null && !countryCode.isBlank()) {
			builder.and(h.countryCode.eq(countryCode));
		}

		if (from != null) {
			builder.and(h.date.goe(from));
		}

		if (to != null) {
			builder.and(h.date.loe(to));
		}

		if (type != null && !type.isBlank()) {
			builder.and(h.type.containsIgnoreCase(type));
		}

		// content 조회
		List<Holiday> content = queryFactory
			.selectFrom(h)
			.where(builder)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.orderBy(h.date.asc())
			.fetch();

		// total count 조회
		Long total = queryFactory
			.select(h.count())
			.from(h)
			.where(builder)
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}
}
