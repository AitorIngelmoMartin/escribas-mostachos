package com.escribasmostachos.Escribasmostachos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;

import lombok.Data;

@Data
public class RegisterRequestDto {
    @NotBlank
    @Email
    @Pattern(regexp = "^(?!.*\\.\\.)([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$", 
             message = "Invalid email format. Please enter a valid email without consecutive dots.")
    private String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",
             message = "Username can only contain letters, numbers, and underscore")
    private String username;

    @NotBlank
    private String password;
}
