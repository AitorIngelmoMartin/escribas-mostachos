package com.escribasmostachos.Escribasmostachos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookDTO {
    
    @NotBlank(message = "Title must not be blank")
    @Size(max = 50, message = "Title must be at most 100 characters")
    private String title;
    
    @NotBlank(message = "Author must not be blank")
    @Size(max = 30, message = "Author must be at most 60 characters")
    private String author;
    
    @Pattern(regexp = "^(https?://.*)?$", message = "Cover URL must be a valid URL")
    private String coverUrl;

    @Size(max = 50, message = "UpdatedBy must be at most 50 characters")
    private String updatedBy;
}
