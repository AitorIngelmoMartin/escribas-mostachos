package com.escribasmostachos.Escribasmostachos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDto;
import com.escribasmostachos.Escribasmostachos.dto.LoginRequestDto;
import com.escribasmostachos.Escribasmostachos.dto.RegisterRequestDto;
import com.escribasmostachos.Escribasmostachos.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto<Void>> registerUser(@Valid @RequestBody RegisterRequestDto registerRequest) {
        authService.register(registerRequest);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ApiResponseDto<Void>(HttpStatus.OK, "user registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto<String>> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {
        String token = authService.login(loginRequest);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ApiResponseDto<String>(HttpStatus.OK, "user logged", token));
    }
}
