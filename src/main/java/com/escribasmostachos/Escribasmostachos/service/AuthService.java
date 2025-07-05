package com.escribasmostachos.Escribasmostachos.service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import com.escribasmostachos.Escribasmostachos.dto.LoginRequestDTO;
import com.escribasmostachos.Escribasmostachos.dto.RegisterRequestDTO;
import com.escribasmostachos.Escribasmostachos.exception.ResourceAlreadyExistsOnDatabaseException;
import com.escribasmostachos.Escribasmostachos.model.RoleType;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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

    public void register(RegisterRequestDTO registerRequest) {
        log.info("Starting register operation");

        Optional<User> optExistingUser = userRepository.findByEmailOrUsername(registerRequest.getEmail(), registerRequest.getUsername());

        if (optExistingUser.isPresent()) {
            User existingUser = optExistingUser.get();

            if (existingUser.getEmail().equals(registerRequest.getEmail())) {
                throw new ResourceAlreadyExistsOnDatabaseException("Email already in use");
            }

            if (existingUser.getUsername().equals(registerRequest.getUsername())) {
                throw new ResourceAlreadyExistsOnDatabaseException("Username already taken");
            }
        }

        log.debug("User email received: " + registerRequest.getEmail());
        User newUser = User.fromDto(registerRequest, passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setMembershipDate(LocalDate.now());
        newUser.setRole(RoleType.ROLE_USER);
        userRepository.save(newUser);
    }

    public String login(LoginRequestDTO loginRequest) {
        log.info("Starting login operation");

        log.debug("User name received: " + loginRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),    
                        loginRequest.getPassword()
                )
        );
        User user = (User) authentication.getPrincipal();
        return jwtService.generateToken(user);
    }
}
