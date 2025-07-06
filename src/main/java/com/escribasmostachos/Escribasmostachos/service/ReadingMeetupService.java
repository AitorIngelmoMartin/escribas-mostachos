package com.escribasmostachos.Escribasmostachos.service;

import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.dto.CreateMeetupDTO;
import com.escribasmostachos.Escribasmostachos.dto.ReadingMeetupDTO;
import com.escribasmostachos.Escribasmostachos.model.Book;
import com.escribasmostachos.Escribasmostachos.model.MeetupStatus;
import com.escribasmostachos.Escribasmostachos.model.ReadingMeetup;
import com.escribasmostachos.Escribasmostachos.repository.ReadingMeetupRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReadingMeetupService {

    private final ReadingMeetupRepository meetupRepository;
    private final UserService userService;
    private final BookService bookService;

    public ReadingMeetupService(ReadingMeetupRepository meetupRepository, 
                                UserService userService,
                                BookService bookService) {
        this.meetupRepository = meetupRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    @Transactional
    public ReadingMeetup createMeetup(Long creatorId, CreateMeetupDTO createMeetupDTO) {
        User creator = userService.getUserById(creatorId);
        Book book = bookService.getBookById(createMeetupDTO.getBookId());

        ReadingMeetup newMeetup = new ReadingMeetup(createMeetupDTO.getTitle(), creator, book);
        newMeetup.addParticipant(creator);
        return meetupRepository.save(newMeetup);
    }

    @Transactional(readOnly = true)
    public List<ReadingMeetupDTO> getMeetups(int limit, int page, MeetupStatus status) {
        PageRequest pageable = PageRequest.of(page, limit);
        List<ReadingMeetup> readingMeetups;
        if (status != null) {
            log.debug("Getting reading meetups with status: "+ status);
            readingMeetups = meetupRepository.findAllByStatus(status, pageable).getContent();
        } else {
            log.debug("Getting all type of meetups");
            readingMeetups = meetupRepository.findAll(pageable).getContent();
        }

        return readingMeetups.stream()
                    .map(ReadingMeetup::toReadingMeetupDTO)
                    .collect(Collectors.toList());
    }
}
