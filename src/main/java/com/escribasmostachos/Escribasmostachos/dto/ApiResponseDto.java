package com.escribasmostachos.Escribasmostachos.dto;

import org.springframework.http.HttpStatus;
import lombok.Data;

@Data
public class ApiResponseDTO<T> {
    private int status;
    private String message;
    private T data;

    public ApiResponseDTO(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }

    public ApiResponseDTO(HttpStatus status, String message, T data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
    }
}
