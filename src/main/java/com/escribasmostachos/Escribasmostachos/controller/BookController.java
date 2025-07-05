package com.escribasmostachos.Escribasmostachos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.escribasmostachos.Escribasmostachos.dto.ApiResponseDTO;
import com.escribasmostachos.Escribasmostachos.dto.BookDTO;
import com.escribasmostachos.Escribasmostachos.dto.BookUpdateDTO;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.service.BookService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
@RestController
@RequestMapping("/books")
public class BookController {
    
    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/isbn/{bookId}")
    public ResponseEntity<ApiResponseDTO<BookDTO>> getBookById(
        @PathVariable
        @Min(value = 1, message = "ID must be zero or positive")
        Long bookId) {

        BookDTO book = bookService.getBookById(bookId);
        return ResponseEntity.ok(new ApiResponseDTO<BookDTO>(HttpStatus.OK, "Books retrieved successfully", book));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<BookDTO>>> getBooks(
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
        List<BookDTO> books = bookService.getBooks(limit, page);
        return ResponseEntity.ok(new ApiResponseDTO<List<BookDTO>>(HttpStatus.OK, "Books retrieved successfully", books));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ApiResponseDTO<BookDTO>> registryBook(@Valid @RequestBody BookDTO bookDto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        BookDTO createdBook = bookService.addBookToDatabase(bookDto, user.getUsername());
        return ResponseEntity.ok(new ApiResponseDTO<BookDTO>(HttpStatus.OK, "Book created successfully", createdBook));
    }

    @PatchMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<?> updateBookInfo(@Valid @RequestBody BookUpdateDTO bookUpdateDto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        boolean somethingWasUpdated = bookService.updateBookFromDatabase(bookUpdateDto, user.getUsername());

        if (!somethingWasUpdated) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(new ApiResponseDTO<Void>(HttpStatus.OK, "book information successfully updated"));
    }
}
