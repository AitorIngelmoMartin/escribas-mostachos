package com.escribasmostachos.Escribasmostachos.dto.profiles;

import java.time.LocalDate;
import java.util.List;

import com.escribasmostachos.Escribasmostachos.dto.books.BookDTO;

import lombok.Data;

@Data
public class UserProfileDTO implements BaseUserProfileDTO{

    private String username;

    private String firstName;

    private String lastName;

    private String profilePictureUrl;

    private Integer booksReadCount;

    private List<BookDTO> currentBooks;

    private LocalDate membershipDate;
}
