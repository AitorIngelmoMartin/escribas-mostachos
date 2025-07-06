package com.escribasmostachos.Escribasmostachos.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateDTO {

    @Size(min = 3, max = 25, message = "First name must be between 3 and 25 characters")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "invalid first name input")
    private String firstName;

    @Size(min = 3, max = 25, message = "Last name must be between 3 and 25 characters")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "invalid last name input")
    private String lastName;

    @Pattern(regexp = "^(https?://.*)?$", message = "Profile picture URL must be a valid URL")
    private String profilePictureUrl;

    private Boolean profileIsPrivate;
}
