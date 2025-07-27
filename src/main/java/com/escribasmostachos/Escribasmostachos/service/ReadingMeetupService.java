package com.escribasmostachos.Escribasmostachos.service;

import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.model.UserBookRead;
import com.escribasmostachos.Escribasmostachos.model.UserMeetupParticipation;
import com.escribasmostachos.Escribasmostachos.dto.meetups.CreateMeetupDTO;
import com.escribasmostachos.Escribasmostachos.dto.meetups.ReadingMeetupDTO;
import com.escribasmostachos.Escribasmostachos.exception.BusinessConflictException;
import com.escribasmostachos.Escribasmostachos.exception.ResourceAlreadyExistsOnDatabaseException;
import com.escribasmostachos.Escribasmostachos.exception.ResourceDontExistsOnDatabaseException;
import com.escribasmostachos.Escribasmostachos.exception.UnauthorizedUserActionException;
import com.escribasmostachos.Escribasmostachos.model.Book;
import com.escribasmostachos.Escribasmostachos.model.MeetupStatus;
import com.escribasmostachos.Escribasmostachos.model.ReadingMeetup;
import com.escribasmostachos.Escribasmostachos.repository.ReadingMeetupRepository;
import com.escribasmostachos.Escribasmostachos.service.mapper.ReadingMeetupMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

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
    private final ReadingService readingService;
    private final ReadingMeetupMapper meetupMapper;
    private final MeetupParticipationService meetupParticipationService;

    public ReadingMeetupService(ReadingMeetupRepository meetupRepository, 
                                UserService userService,
                                BookService bookService,
                                ReadingService readingService,
                                ReadingMeetupMapper meetupMapper,
                                MeetupParticipationService meetupParticipationService) {
        this.meetupRepository = meetupRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.readingService = readingService;
        this.meetupMapper = meetupMapper;
        this.meetupParticipationService = meetupParticipationService;
    }

    @Transactional
    public ReadingMeetupDTO createMeetup(Long creatorId, CreateMeetupDTO createMeetupDTO) {
        User creator = userService.getUserById(creatorId);
        Book book = bookService.getBookById(createMeetupDTO.getBookId());
        
        Optional<ReadingMeetup> existingMeetup = meetupRepository.findByCreatorIdAndBookIdAndStatus(creator.getId(), book.getId(), MeetupStatus.DRAFT);
        if(existingMeetup.isPresent()){
            throw new ResourceAlreadyExistsOnDatabaseException("You already have a draft of a meetup to read that book");
        }

        ReadingMeetup newMeetup = new ReadingMeetup(createMeetupDTO.getTitle(), creator, book);
        meetupRepository.save(newMeetup);
        meetupParticipationService.addParticipant(newMeetup, creator);
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
        ReadingMeetup meetup = findReadingMeetupById(readingMeetupId);
        if (!meetup.getStatus().equals(MeetupStatus.DRAFT)) {
            throw new BusinessConflictException("You can't join a meetup that is not in DRAFT status");
        }

        boolean alreadyJoined = meetup.getParticipants().stream()
            .anyMatch(participant -> participant.getUser().getId().equals(userId));

        if (alreadyJoined) {
            throw new ResourceAlreadyExistsOnDatabaseException("User is already in the meetup");
        }

        User newParticipant = userService.getUserById(userId);
        meetupParticipationService.addParticipant(meetup, newParticipant);
    }

    @Transactional
    public void leaveMeetup(Long readingMeetupId, Long userId) {
        ReadingMeetup meetup = findReadingMeetupById(readingMeetupId);

        Optional<UserMeetupParticipation> participationOpt = meetup.getParticipants().stream()
            .filter(p -> p.getUser().getId().equals(userId))
            .findFirst();
        if (participationOpt.isEmpty()) {
            throw new ResourceDontExistsOnDatabaseException("User not registered in that meetup");
        }

        User userToRemove = userService.getUserById(userId);
        meetupParticipationService.removeParticipant(meetup, userToRemove);
    }

    public void completeMeetup(Long readingMeetupId, Long userId) {
        ReadingMeetup readingMeetupToComplete = findReadingMeetupById(readingMeetupId);
        User userToAdd = userService.getUserById(userId);
        
        meetupParticipationService.markAsCompleted(readingMeetupToComplete, userToAdd);
    }

    @Transactional
    public void deleteMeetup(Long readingMeetupId, Long userId) {
        ReadingMeetup readingMeetupToDelete = findReadingMeetupById(readingMeetupId);
        meetupRepository.deleteById(readingMeetupToDelete.getId());
    }

    @Transactional
    public ReadingMeetupDTO changeMeetupStatus(Long readingMeetupId, Long userId, MeetupStatus status) {
        ReadingMeetup readingMeetupToUpdate = findReadingMeetupById(readingMeetupId);

        if(readingMeetupToUpdate.getCreator().getId() != userId){
            throw new UnauthorizedUserActionException("You can only update status from your own meetups");
        }
        MeetupStatus currentMeetupStatus = readingMeetupToUpdate.getStatus();
        if(currentMeetupStatus.equals(MeetupStatus.CANCELLED)){
            throw new UnauthorizedUserActionException("You can't update meetups canceled");
        }

        if(!currentMeetupStatus.equals(status)){
            if(status.equals(MeetupStatus.COMPLETED)){
                if (!currentMeetupStatus.equals(MeetupStatus.ACTIVE)){
                    throw new BusinessConflictException("You can't update meetups to 'COMPLETED' if they weren't in 'ACTIVE' status");
                }
                processCompletion(readingMeetupToUpdate);
            }else if(status.equals(MeetupStatus.ACTIVE)){
                processActivation(readingMeetupToUpdate);
            }else if (status.equals(MeetupStatus.CANCELLED)) {
                processCancellation(readingMeetupToUpdate);
            }else if (status.equals(MeetupStatus.DRAFT)){
                processBackToDraft(readingMeetupToUpdate);
            }
            readingMeetupToUpdate.setStatus(status);
        }


        return meetupMapper.toReadingMeetupDTO(readingMeetupToUpdate);
    }

    private void processCompletion(ReadingMeetup meetup) {
        Book book = meetup.getBook();
        Set<User> participants = meetup.getParticipants().stream()
            .map(UserMeetupParticipation::getUser)
            .collect(Collectors.toSet());

        List<Long> userIds = participants.stream().map(User::getId).toList();
        List<UserBookRead> existingReadings = readingService.getBooksReadingByBookIdAndUserIds(book.getId(), userIds);

        Set<Long> alreadyHasReading = existingReadings.stream()
            .map(r -> r.getUser().getId())
            .collect(Collectors.toSet());

        List<UserBookRead> newReadings = new ArrayList<>();
        Set<User> participantsToUpdate = new HashSet<>();
        for (UserMeetupParticipation participation : meetup.getParticipants()) {
            User user = participation.getUser();
            if (!alreadyHasReading.contains(user.getId()) && participation.isCompleted() ) {
                user.setBooksReadCount(user.getBooksReadCount() + 1);
                participantsToUpdate.add(user);

                UserBookRead newUserBookRead = new UserBookRead(user, book);
                newUserBookRead.markBookAsRead();
                newReadings.add(newUserBookRead);
            }
        }
        meetup.setMeetupEndDate(LocalDate.now());

        userService.saveAll(participantsToUpdate);
        readingService.saveAll(newReadings);
    }

    private void processActivation(ReadingMeetup meetup) {
        meetup.setMeetupStartDate(LocalDate.now());
        meetupRepository.save(meetup);
    }

    private void processCancellation(ReadingMeetup meetup) {
        meetup.setMeetupEndDate(LocalDate.now());

        meetupRepository.save(meetup);
        userService.saveAll(
            meetup.getParticipants().stream()
                .map(UserMeetupParticipation::getUser)
                .collect(Collectors.toSet())
        );
    }

    private void processBackToDraft(ReadingMeetup meetup) {
        meetup.setMeetupStartDate(null);
        meetupRepository.save(meetup);
    }

    public ReadingMeetup findReadingMeetupById(Long readingMeetupId){
        return meetupRepository.findById(readingMeetupId).orElseThrow(() ->new ResourceDontExistsOnDatabaseException("No meeting found")); 
    }
}
