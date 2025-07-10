package com.escribasmostachos.Escribasmostachos.dto.books;

import com.escribasmostachos.Escribasmostachos.validations.IsbnValid;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookReadingDTO {
    
    @NotBlank(message = "ISBN must not be blank")
    @IsbnValid(message = "The ISBN must be valid ISBN-10 or ISBN-13")
    private String isbn;
}
