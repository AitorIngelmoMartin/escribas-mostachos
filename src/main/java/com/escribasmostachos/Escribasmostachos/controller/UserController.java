package com.escribasmostachos.Escribasmostachos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDto;
import com.escribasmostachos.Escribasmostachos.dto.ProfileUpdateDto;
import com.escribasmostachos.Escribasmostachos.dto.UserProfileDto;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<UserProfileDto>> getProfile(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        UserProfileDto userProfileDto = userService.getProfileByUserId(user.getId());
        return ResponseEntity.ok(new ApiResponseDto<UserProfileDto>(HttpStatus.OK, "user profile obtained", userProfileDto));
    }

    @PatchMapping
    public ResponseEntity<ApiResponseDto<Void>> updateUserProfile(@Valid @RequestBody ProfileUpdateDto dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean somethingWasUpdated = userService.updateUserProfile(dto, user.getId());

        String message = somethingWasUpdated
            ? "user profile successfully updated"
            : "nothing to update";
        return ResponseEntity.ok(new ApiResponseDto<Void>(HttpStatus.OK, message));
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponseDto<UserProfileDto>> getUserProfile(
            @PathVariable
            @Size(min = 3, max = 20, message = "Invalid username size")
            @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Invalid username format")
            String username) {
        UserProfileDto userProfileDto = userService.getProfileByUsername(username);

        return ResponseEntity.ok(new ApiResponseDto<UserProfileDto>(HttpStatus.OK, "user profile obtained", userProfileDto));
    }
}
