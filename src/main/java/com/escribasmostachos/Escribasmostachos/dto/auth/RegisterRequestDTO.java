package com.escribasmostachos.Escribasmostachos.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;

import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank
    @Email
    @Pattern(regexp = "^(?!.*\\.\\.)([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$", 
             message = "Invalid email format. Please enter a valid email without consecutive dots.")
    private String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9_]+$",
             message = "Username can only contain letters, numbers, and underscore")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank
    private String password;

    public void setEmail(String email) {
        this.email = (email != null) ? email.toLowerCase() : null;
    }

    public void setUsername(String username) {
        this.username = (username != null) ? username.toLowerCase() : null;
    }
}
