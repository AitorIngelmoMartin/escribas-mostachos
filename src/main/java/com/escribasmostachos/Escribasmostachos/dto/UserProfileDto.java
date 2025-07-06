package com.escribasmostachos.Escribasmostachos.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Data
public class UserProfileDTO {

    private String username;

    private String firstName;

    private String lastName;

    private String profilePictureUrl;

    private Integer booksReadCount;

    private List<BookDTO> currentBooks;

    private Set<ReadingMeetupDTO> readingMeetups;

    private LocalDate membershipDate;
}
