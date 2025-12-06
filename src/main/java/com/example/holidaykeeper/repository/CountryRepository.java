package com.example.holidaykeeper.repository;

import com.example.holidaykeeper.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
	Optional<Country> findByCountryCode(String countryCode);
}