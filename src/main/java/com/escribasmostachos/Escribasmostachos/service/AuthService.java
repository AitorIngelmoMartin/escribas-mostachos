package com.escribasmostachos.Escribasmostachos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.escribasmostachos.Escribasmostachos.dto.LoginRequestDto;
import com.escribasmostachos.Escribasmostachos.dto.RegisterRequestDto;
import com.escribasmostachos.Escribasmostachos.exception.UserAlreadyExistsException;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthService {
    
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequestDto registerRequest) {
        log.info("Starting register operation");

        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new UserAlreadyExistsException("Email " + registerRequest.getEmail() + " already used");
        }

        log.info("User email received: " + registerRequest.getEmail());
        User newUser = User.fromDto(registerRequest, passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(newUser);
    }

    public String login(LoginRequestDto loginRequest) {
        log.info("Starting login operation");

        log.info("User email received: " + loginRequest.getEmail());
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
