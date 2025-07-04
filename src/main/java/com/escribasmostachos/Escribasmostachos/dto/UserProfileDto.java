package com.escribasmostachos.Escribasmostachos.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class UserProfileDTO {

    private String username;

    private String firstName;

    private String lastName;

    private String profilePictureUrl;

    private Integer booksReadCount;

    private List<BookDTO> currentBooks;

    private LocalDate membershipDate;
}
