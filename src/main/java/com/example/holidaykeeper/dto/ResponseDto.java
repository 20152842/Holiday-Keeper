package com.example.holidaykeeper.dto;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto<T> {

	private final int code;
	private final String status;
	private final String message;
	private final T data;

	// 모든 필드를 직접 지정할 수 있는 생성자
	public ResponseDto(int code, String status, String message, T data) {
		this.code = code;
		this.status = status;
		this.message = message;
		this.data = data;
	}

	// HttpStatus + 메시지 + 데이터
	public ResponseDto(HttpStatus httpStatus, String message, T data) {
		this.code = httpStatus.value();
		this.status = httpStatus.getReasonPhrase();
		this.message = message;
		this.data = data;
	}

	// 성공: 200 OK, 기본 메시지, 데이터 포함
	public static <T> ResponseDto<T> success(T data) {
		return new ResponseDto<>(HttpStatus.OK, "성공적으로 처리되었습니다.", data);
	}

	// 성공: 커스텀 상태, 데이터 포함
	public static <T> ResponseDto<T> success(
		HttpStatus status,
		String message,
		T data
	) {
		return new ResponseDto<>(status, message, data);
	}

	// uri location을 담은 응답을 위한 메서드
	public static ResponseDto<Void> responseWithLocation(
		HttpStatus status,
		URI location,
		String message
	) {
		ServletRequestAttributes attrs =
			(ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (attrs != null && attrs.getResponse() != null) {
			attrs.getResponse().setHeader("Location", location.toString());
		}
		return new ResponseDto<>(status, message, null);
	}

	// NO CONTENT 처럼 데이터 없이 상태+메시지
	public static ResponseDto<Void> responseWithNoData(
		HttpStatus status,
		String message
	) {
		return new ResponseDto<>(status, message, null);
	}
}