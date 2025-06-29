package com.escribasmostachos.Escribasmostachos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.escribasmostachos.Escribasmostachos.dto.ProfileUpdateDto;
import com.escribasmostachos.Escribasmostachos.dto.UserProfileDto;
import com.escribasmostachos.Escribasmostachos.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProfileService {

    private final UserService userService;

    public ProfileService(UserService userService) {
        this.userService = userService;
    }


    public UserProfileDto getProfile(String username){
        return userService.loadUserByUsername(username).toProfileDto();
    }

    @Transactional
    public boolean updateUserProfile(ProfileUpdateDto dto, String username){

        if (!haveSomethingToUpdate(dto)){
            return false;
        }
        
        User oldUserInfo = userService.loadUserByUsername(username);
        oldUserInfo.updatePropertiesFromDto(dto);
        return true;
    }

    private boolean haveSomethingToUpdate(ProfileUpdateDto dto) {
        return (dto.getFirstName() != null) ||
            (dto.getLastName() != null) ||
            (dto.getProfilePictureUrl() != null);
    }
}
