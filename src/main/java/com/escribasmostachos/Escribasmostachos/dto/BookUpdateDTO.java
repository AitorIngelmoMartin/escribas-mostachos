package com.escribasmostachos.Escribasmostachos.dto;

import com.escribasmostachos.Escribasmostachos.validations.IsbnValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookUpdateDTO {

    @NotBlank(message = "ISBN must not be blank")
    @IsbnValid(message = "The ISBN must be valid ISBN-10 or ISBN-13")
    private String bookIsbn;

    private String newIsbn;

    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;
    
    @Size(max = 100, message = "Author cannot exceed 100 characters")
    private String author;
    
    @Pattern(regexp = "^(https?://.*)?$", message = "Cover URL must be a valid URL")
    private String coverUrl;
}
