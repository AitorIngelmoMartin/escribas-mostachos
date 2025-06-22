package com.escribasmostachos.Escribasmostachos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escribasmostachos.Escribasmostachos.dto.LoginRequest;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.service.JwtService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public String authenticateUser(@RequestBody LoginRequest loginRequest) {
        log.info("Starting login operation");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),    
                        loginRequest.getPassword()
                )
        );
        User user = (User) authentication.getPrincipal();
        return jwtService.generateToken(user);
    }
}
