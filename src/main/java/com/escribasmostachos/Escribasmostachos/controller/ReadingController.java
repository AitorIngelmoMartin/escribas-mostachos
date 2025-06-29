package com.escribasmostachos.Escribasmostachos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDto;
import com.escribasmostachos.Escribasmostachos.dto.BookDTO;
import com.escribasmostachos.Escribasmostachos.dto.BookReadingDto;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.service.ReadingService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/readings")
public class ReadingController {
 
    private final ReadingService readingService;

    public ReadingController(ReadingService readingService){
        this.readingService = readingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<Void>> markBookAsRead(@Valid @RequestBody BookReadingDto dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        boolean readAdded = readingService.markBookAsRead(dto, user.getId());
        String message = readAdded
            ? "Read successfully registered"
            : "Read already registered";
        return ResponseEntity.ok(new ApiResponseDto<Void>(HttpStatus.OK, message));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponseDto<List<BookDTO>>> getUserReadBooks(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<BookDTO> booksRead = readingService.getBooksReadByUser(user.getId());

        return ResponseEntity.ok(new ApiResponseDto<List<BookDTO>>(HttpStatus.OK, "Books retrieved successfully", booksRead));
    }
}
