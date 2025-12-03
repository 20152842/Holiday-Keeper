package com.example.holidaykeeper.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientConfig {
	@Value("${holiday.nager.base-url}")
	private String nagerBaseUrl;

	@Bean("nagerWebClient")
	public WebClient nagerWebClient() {
		return WebClient.builder()
			.baseUrl(nagerBaseUrl)
			.build();
	}
}
