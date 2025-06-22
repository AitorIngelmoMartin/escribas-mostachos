package com.escribasmostachos.Escribasmostachos.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserProfileDto {

    private String username;

    private String firstName;

    private String lastName;

    private String profilePictureUrl;

    // private List<String> favoriteGenres;

    private Integer booksReadCount;

    private BookDTO currentBook;

    private LocalDate membershipDate;

    // private List<String> roles;

    private LocalDateTime lastActive;
}
