package com.escribasmostachos.Escribasmostachos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookDTO {
    
    @NotBlank
    private String title;
    
    @NotBlank
    private String author;
    
    private String coverUrl;

    private UserProfileDto addedBy;
}
