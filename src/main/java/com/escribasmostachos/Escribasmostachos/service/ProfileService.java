package com.escribasmostachos.Escribasmostachos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.escribasmostachos.Escribasmostachos.dto.UserProfileDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfileService {
    
    @Autowired
    private UserService userService;

    public ProfileService(UserService userService) {
        this.userService = userService;
    }

    public UserProfileDto getProfile(String email){
        return userService.loadUserByUsername(email).toProfileDto();
    }
}
