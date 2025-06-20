package com.escribasmostachos.Escribasmostachos.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escribasmostachos.Escribasmostachos.dto.AuthResponse;
import com.escribasmostachos.Escribasmostachos.dto.LoginRequest;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.service.JwtService;
import com.escribasmostachos.Escribasmostachos.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (userOpt.isPresent()) {
            String userJwt = jwtService.generateToken(userOpt.get());
            return ResponseEntity.ok(new AuthResponse(userJwt));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
