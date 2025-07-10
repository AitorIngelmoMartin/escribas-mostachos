package com.escribasmostachos.Escribasmostachos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDTO;
import com.escribasmostachos.Escribasmostachos.dto.books.BookDTO;
import com.escribasmostachos.Escribasmostachos.dto.books.BookReadingDTO;
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

    @PostMapping("/start")
    public ResponseEntity<ApiResponseDTO<Void>> addCurrentBook(@Valid @RequestBody BookReadingDTO dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        readingService.addCurrentBook(dto, user.getId());

        return ResponseEntity.ok(new ApiResponseDTO<Void>(HttpStatus.OK, "Read successfully registered"));
    }

    @PostMapping("/complete")
    public ResponseEntity<ApiResponseDTO<Void>> markBookAsRead(@Valid @RequestBody BookReadingDTO dto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        readingService.markBookAsRead(dto, user.getId());

        return ResponseEntity.ok(new ApiResponseDTO<Void>(HttpStatus.OK, "Read successfully registered"));
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponseDTO<List<BookDTO>>> getUserReadBooks(Authentication authentication,
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "0") int page) {
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
        User user = (User) authentication.getPrincipal();
        List<BookDTO> booksRead = readingService.getBooksReadByUser(user.getId(), limit, page);

        return ResponseEntity.ok(new ApiResponseDTO<List<BookDTO>>(HttpStatus.OK, "Books retrieved successfully", booksRead));
    }
}
