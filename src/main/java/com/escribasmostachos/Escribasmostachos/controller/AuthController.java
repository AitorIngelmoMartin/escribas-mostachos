package com.escribasmostachos.Escribasmostachos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDto;
import com.escribasmostachos.Escribasmostachos.dto.LoginRequestDto;
import com.escribasmostachos.Escribasmostachos.dto.RegisterRequestDto;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.repository.UserRepository;
import com.escribasmostachos.Escribasmostachos.service.JwtService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthController(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> registerUser(@RequestBody RegisterRequestDto registerRequest) {
        log.info("Starting register operation");
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new DataIntegrityViolationException("Email " + registerRequest.getEmail() + " already used");
        }

        log.info("User received: " + registerRequest);
        User newUser = User.fromDto(registerRequest, passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(newUser);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ApiResponseDto(HttpStatus.OK, "user registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> authenticateUser(@RequestBody LoginRequestDto loginRequest) {
        log.info("Starting login operation");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),    
                        loginRequest.getPassword()
                )
        );
        User user = (User) authentication.getPrincipal();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ApiResponseDto(HttpStatus.OK, jwtService.generateToken(user)));
    }
}
