package com.example.holidaykeeper.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "country_code", length = 10, unique = true, nullable = false)
	private String countryCode;

	@Column(name = "name", length = 255, nullable = false)
	private String name;
}
