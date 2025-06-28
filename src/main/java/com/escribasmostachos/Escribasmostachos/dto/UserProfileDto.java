package com.escribasmostachos.Escribasmostachos.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UserProfileDto {

    private String username;

    private String firstName;

    private String lastName;

    private String profilePictureUrl;

    private Integer booksReadCount;

    private BookDTO currentBook;

    private LocalDate membershipDate;
}
