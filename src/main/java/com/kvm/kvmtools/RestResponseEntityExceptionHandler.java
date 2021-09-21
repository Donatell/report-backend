package com.kvm.kvmtools;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler
		extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(NullPointerException.class)
	private ResponseEntity<Object> handleNull(NullPointerException ex, WebRequest request) {
		return handleExceptionInternal(ex, "Серверная ошибка: NullPointerException", new HttpHeaders(), HttpStatus.CONFLICT, request);
	}
	
	@ExceptionHandler(Exception.class)
	private ResponseEntity<Object> handleConflict(Exception ex, WebRequest request) {
		return handleExceptionInternal(ex, "Серверная ошибка: " + ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT, request);
	}
	
}