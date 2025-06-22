package com.escribasmostachos.Escribasmostachos.dto;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ApiResponseDto {
    private int status;
    private String message;

    public ApiResponseDto(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }
}
