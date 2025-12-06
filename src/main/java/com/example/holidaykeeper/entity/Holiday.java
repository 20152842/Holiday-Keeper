package com.example.holidaykeeper.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "holidays")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holiday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "country_code", length = 10, nullable = false)
	private String countryCode;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@Column(name = "local_name", length = 255)
	private String localName;

	@Column(name = "name", length = 255)
	private String name;

	@Column(name = "fixed")
	private Boolean fixed;

	@Column(name = "global")
	private Boolean global;

	@Column(name = "type", length = 255)
	private String type;

	@Column(name = "counties", columnDefinition = "TEXT")
	private String counties;

	@Column(name = "launch_year")
	private Integer launchYear;

	@Column(name = "created_at")
	private java.time.OffsetDateTime createdAt;
}
