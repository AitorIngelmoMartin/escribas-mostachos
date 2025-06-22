package com.escribasmostachos.Escribasmostachos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDto;
import com.escribasmostachos.Escribasmostachos.dto.UserProfileDto;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.service.ProfileService;

@RestController
@RequestMapping("/profile")
public class ProfileController {
    
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<UserProfileDto>> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserProfileDto userProfileDto = profileService.getProfile(user.getEmail());
        return ResponseEntity.ok(new ApiResponseDto<UserProfileDto>(HttpStatus.OK, "user profile obtained", userProfileDto));
    }

}
