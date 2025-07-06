package com.escribasmostachos.Escribasmostachos.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.escribasmostachos.Escribasmostachos.dto.BaseUserProfileDTO;
import com.escribasmostachos.Escribasmostachos.dto.ProfileUpdateDTO;
import com.escribasmostachos.Escribasmostachos.dto.UserProfileDTO;
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
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getUserById(Long userId){
        log.info("Finding user with userId: " + userId);
        return userRepository.findById(userId).orElseThrow(() ->new UsernameNotFoundException("User not found"));
    }

    public UserProfileDTO getProfileByUserId(Long userId){
        log.info("Finding user with userId: " +  userId);
        return getUserById(userId).toProfileDto();
    }

    public BaseUserProfileDTO getProfileByUsername(String username){
        User userToGetProfile = loadUserByUsername(username);

        BaseUserProfileDTO profileToReturn;
        if (userToGetProfile.getProfileIsPrivate()){
            profileToReturn = userToGetProfile.toPrivateUserProfileDTO();
        }else{
            profileToReturn = userToGetProfile.toProfileDto();
        }  
        return profileToReturn;
    }

    @Transactional
    public boolean updateUserProfile(ProfileUpdateDTO dto, Long userId){

        if (!haveSomethingToUpdate(dto)){
            return false;
        }
        
        User oldUserInfo = getUserById(userId);
        oldUserInfo.updatePropertiesFromDto(dto);
        return true;
    }

    private boolean haveSomethingToUpdate(ProfileUpdateDTO dto) {
        return (dto.getFirstName() != null) ||
            (dto.getLastName() != null) ||
            (dto.getProfileIsPrivate() != null) ||
            (dto.getProfilePictureUrl() != null);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
