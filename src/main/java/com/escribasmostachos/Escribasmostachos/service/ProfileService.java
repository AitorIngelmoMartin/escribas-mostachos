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

        if (allFieldsNull(dto)){
            return false;
        }
        
        User oldUserInfo = userService.loadUserByUsername(username);
        if (dto.getFirstName() != null) {
            oldUserInfo.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            oldUserInfo.setLastName(dto.getLastName());
        }
        if (dto.getProfilePictureUrl() != null) {
            oldUserInfo.setProfilePictureUrl(dto.getProfilePictureUrl());
        }

        return true;
    }

    private boolean allFieldsNull(ProfileUpdateDto dto) {
    return dto.getFirstName() == null &&
           dto.getLastName() == null &&
           dto.getProfilePictureUrl() == null;
    }
}
