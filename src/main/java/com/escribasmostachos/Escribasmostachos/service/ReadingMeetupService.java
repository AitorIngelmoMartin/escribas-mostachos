package com.escribasmostachos.Escribasmostachos.service;

import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.dto.meetups.CreateMeetupDTO;
import com.escribasmostachos.Escribasmostachos.dto.meetups.ReadingMeetupDTO;
import com.escribasmostachos.Escribasmostachos.exception.ResourceDontExistsOnDatabaseException;
import com.escribasmostachos.Escribasmostachos.model.Book;
import com.escribasmostachos.Escribasmostachos.model.MeetupStatus;
import com.escribasmostachos.Escribasmostachos.model.ReadingMeetup;
import com.escribasmostachos.Escribasmostachos.repository.ReadingMeetupRepository;
import com.escribasmostachos.Escribasmostachos.service.mapper.ReadingMeetupMapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
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
    private final ReadingMeetupMapper meetupMapper;

    public ReadingMeetupService(ReadingMeetupRepository meetupRepository, 
                                UserService userService,
                                BookService bookService,
                                ReadingMeetupMapper meetupMapper) {
        this.meetupRepository = meetupRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.meetupMapper = meetupMapper;
    }

    @Transactional
    public ReadingMeetupDTO createMeetup(Long creatorId, CreateMeetupDTO createMeetupDTO) {
        User creator = userService.getUserById(creatorId);
        Book book = bookService.getBookById(createMeetupDTO.getBookId());

        ReadingMeetup newMeetup = new ReadingMeetup(createMeetupDTO.getTitle(), creator, book);
        newMeetup.addParticipant(creator);
        meetupRepository.save(newMeetup);

        return meetupMapper.toReadingMeetupDTO(newMeetup);
    }

    @Transactional(readOnly = true)
    public List<ReadingMeetupDTO> getMeetups(Long userId, int limit, int page, MeetupStatus status,  String usernameToFilter) {
        PageRequest pageable = PageRequest.of(page, limit);
        Page<ReadingMeetup> readingMeetups;

        Long userIdToFind = null;
        if(userId != null){
            userIdToFind = userId;
        }else if(usernameToFilter != null){
            userIdToFind = userService.loadUserByUsername(usernameToFilter).getId();
        }

        if (status != null && userIdToFind != null) {
            log.debug("Getting reading meetups with status: "+ status + " created by user " + userIdToFind);
            readingMeetups = meetupRepository.findAllByStatusAndCreatorId(status, userIdToFind, pageable);
        } else if (userIdToFind != null) {
            log.debug("Getting all type of meetups created by user: " + userIdToFind);
            readingMeetups = meetupRepository.findAllByCreatorId(userIdToFind, pageable);
        } else if (status != null) {
            log.debug("Getting reading meetups with status: "+ status);
            readingMeetups = meetupRepository.findAllByStatus(status, pageable);
        } else {
            log.debug("Getting all type of meetups");
            readingMeetups = meetupRepository.findAll(pageable);
        }

        return readingMeetups.getContent().stream()
                    .map(meetupMapper::toReadingMeetupDTO)
                    .collect(Collectors.toList());
    }

    @Transactional
    public void joinToMeetup(Long readingMeetupId, Long userId) {
        ReadingMeetup readingMeetupToJoin = findReadingMeetupById(readingMeetupId);
        User userToAdd = userService.getUserById(userId);
        readingMeetupToJoin.addParticipant(userToAdd);
    }

    private ReadingMeetup findReadingMeetupById(Long readingMeetupId){
        return meetupRepository.findById(readingMeetupId).orElseThrow(() ->new ResourceDontExistsOnDatabaseException("No meeting found")); 
    }
}
