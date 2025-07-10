package com.escribasmostachos.Escribasmostachos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDTO;
import com.escribasmostachos.Escribasmostachos.dto.auth.LoginRequestDTO;
import com.escribasmostachos.Escribasmostachos.dto.auth.RegisterRequestDTO;
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
    public ResponseEntity<ApiResponseDTO<Void>> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        authService.register(registerRequest);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ApiResponseDTO<Void>(HttpStatus.OK, "user successfully registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<String>> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        String token = authService.login(loginRequest);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ApiResponseDTO<String>(HttpStatus.OK, "user successfully logged", token));
    }
}
