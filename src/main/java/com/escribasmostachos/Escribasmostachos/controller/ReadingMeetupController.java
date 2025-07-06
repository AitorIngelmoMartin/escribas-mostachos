package com.escribasmostachos.Escribasmostachos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDTO;
import com.escribasmostachos.Escribasmostachos.dto.meetups.CreateMeetupDTO;
import com.escribasmostachos.Escribasmostachos.dto.meetups.ReadingMeetupDTO;
import com.escribasmostachos.Escribasmostachos.model.MeetupStatus;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.service.ReadingMeetupService;
import com.escribasmostachos.Escribasmostachos.utils.PublicIdGenerator;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/meetups")
public class ReadingMeetupController {
    
    private final ReadingMeetupService readingMeetupService;
    private final PublicIdGenerator publicIdGenerator;

    public ReadingMeetupController(ReadingMeetupService readingMeetupService, PublicIdGenerator publicIdGenerator){
        this.readingMeetupService = readingMeetupService;
        this.publicIdGenerator = publicIdGenerator;
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ReadingMeetupDTO>>> getMeetups(
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(required = false) MeetupStatus status,
        @RequestParam(required = false, defaultValue = "false") boolean ownOnly,
        @RequestParam(required = false) String creatorUsername,
        Authentication authentication) {
        List<Integer> allowedLimits = List.of(10, 25, 50);

        if (!allowedLimits.contains(limit)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(
                HttpStatus.BAD_REQUEST,
                "Invalid 'limit' value. Allowed values are 10, 25, or 50."
            ));
        }

        if (page < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponseDTO<>(
                HttpStatus.BAD_REQUEST,
                "Invalid 'page' value. The value must be greater than or equal to zero."
            ));
        }

        String usernameToFilter = null;
        Long userId = null;
        if (ownOnly) {
            User user = (User) authentication.getPrincipal();
            userId = user.getId();
        } else if (creatorUsername != null && !creatorUsername.isBlank()) {
            usernameToFilter = creatorUsername;
        }

        List<ReadingMeetupDTO> readingMeetup = readingMeetupService.getMeetups(userId, limit, page, status, usernameToFilter);
        return ResponseEntity.ok(new ApiResponseDTO<List<ReadingMeetupDTO>>(HttpStatus.OK, "Reading meetup draft created successfully", readingMeetup));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<ReadingMeetupDTO>> createMeetup(@Valid @RequestBody CreateMeetupDTO createMeetupDTO, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        ReadingMeetupDTO readingMeetup = readingMeetupService.createMeetup(user.getId(), createMeetupDTO);
        return ResponseEntity.ok(new ApiResponseDTO<ReadingMeetupDTO>(HttpStatus.CREATED, "Reading meetup draft created successfully", readingMeetup));
    }

    @PostMapping("/{publicId}/join")
    public ResponseEntity<ApiResponseDTO<Void>> joinToMeetup(@PathVariable String publicId, Authentication authentication) {
        Long readingMeetupId = publicIdGenerator.decode(publicId);
        User user = (User) authentication.getPrincipal();
        
        readingMeetupService.joinToMeetup(readingMeetupId, user.getId());

        return ResponseEntity.ok(new ApiResponseDTO<Void>(HttpStatus.OK, "User added to reading meetup"));
    }
}
