package com.escribasmostachos.Escribasmostachos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank
    @Email
    @Pattern(regexp = "^(?!.*\\.\\.)([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$", 
             message = "Invalid email format. Please enter a valid email without consecutive dots.")
    private String email;

    @NotBlank
    private String password;
}
