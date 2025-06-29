package com.escribasmostachos.Escribasmostachos.dto;

import com.escribasmostachos.Escribasmostachos.validations.IsbnValid;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookReadingDto {
    
    @NotBlank(message = "ISBN must not be blank")
    @IsbnValid(message = "The ISBN must be valid ISBN-10 or ISBN-13")
    private String isbn;
}
