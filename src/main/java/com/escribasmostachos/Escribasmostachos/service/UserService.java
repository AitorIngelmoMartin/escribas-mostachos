package com.escribasmostachos.Escribasmostachos.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.escribasmostachos.Escribasmostachos.dto.ProfileUpdateDto;
import com.escribasmostachos.Escribasmostachos.dto.UserProfileDto;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Finding user with username: " +  username);
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent())  {
            throw new UsernameNotFoundException("User not found");
        }
        return userOpt.get();
    }

    public UserProfileDto getProfile(String username){
        return loadUserByUsername(username).toProfileDto();
    }

    @Transactional
    public boolean updateUserProfile(ProfileUpdateDto dto, String username){

        if (!haveSomethingToUpdate(dto)){
            return false;
        }
        
        User oldUserInfo = loadUserByUsername(username);
        oldUserInfo.updatePropertiesFromDto(dto);
        return true;
    }

    private boolean haveSomethingToUpdate(ProfileUpdateDto dto) {
        return (dto.getFirstName() != null) ||
            (dto.getLastName() != null) ||
            (dto.getProfilePictureUrl() != null);
    }
}
