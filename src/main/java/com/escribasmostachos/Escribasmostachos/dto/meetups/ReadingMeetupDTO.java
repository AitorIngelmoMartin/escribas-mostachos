package com.escribasmostachos.Escribasmostachos.dto.meetups;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.escribasmostachos.Escribasmostachos.dto.books.BookDTO;
import com.escribasmostachos.Escribasmostachos.model.MeetupStatus;

import lombok.Data;

@Data
public class ReadingMeetupDTO {
    
    private String publicId;

    private String title;

    private String creator;

    private BookDTO book;

    private MeetupStatus status = MeetupStatus.DRAFT;

    private Set<String> participants = new HashSet<>();

    private LocalDate meetupStartDate;

    private LocalDate meetupEndDate;
}
