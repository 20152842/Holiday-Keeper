package com.example.holidaykeeper.external.config;


import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
@Configuration
public class SwaggerConfig {

	/**
	 * 전체 OpenAPI 문서의 메타 정보 설정
	 */
	@Bean
	public OpenAPI holidayKeeperOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Holiday-Keeper API")
				.description("Nager.Date 기반 전 세계 공휴일 저장·검색·동기화 서비스")
				.version("v1.0.0"));
	}

	@Bean
	public GroupedOpenApi holidayKeeperApi() {
		return GroupedOpenApi.builder()
			.group("holiday-keeper-v1")
			.pathsToMatch("/api/v1/**")
			.build();
	}
}