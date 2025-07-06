package com.escribasmostachos.Escribasmostachos.dto.profiles;

import lombok.Data;

@Data
public class PrivateUserProfileDTO implements BaseUserProfileDTO{
    private String username;

    private String profilePictureUrl;

    private boolean profileIsPrivate = true;
}
