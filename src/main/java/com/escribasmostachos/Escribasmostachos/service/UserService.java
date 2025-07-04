package com.escribasmostachos.Escribasmostachos.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent())  {
            throw new UsernameNotFoundException("User not found");
        }
        return userOpt.get();
    }

    public User getUserById(Long userId){
        log.info("Finding user with userId: " + userId);
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent())  {
            throw new UsernameNotFoundException("User not found");
        }
        return userOpt.get();
    }

    public UserProfileDTO getProfileByUserId(Long userId){
        log.info("Finding user with userId: " +  userId);
        return getUserById(userId).toProfileDto();
    }

    public UserProfileDTO getProfileByUsername(String username){
        return loadUserByUsername(username).toProfileDto();
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
            (dto.getProfilePictureUrl() != null);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
