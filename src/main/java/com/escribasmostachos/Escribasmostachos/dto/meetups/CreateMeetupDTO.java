package com.escribasmostachos.Escribasmostachos.dto.meetups;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateMeetupDTO {

    @NotNull(message = "Book ID must not be blank")
    @Min(value = 1, message = "Book ID must be zero or positive")
    private Long bookId;

    @NotBlank(message = "Title ID must not be blank")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;
}
