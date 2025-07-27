package com.escribasmostachos.Escribasmostachos.service;

import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.model.UserBookRead;
import com.escribasmostachos.Escribasmostachos.model.UserMeetupParticipation;
import com.escribasmostachos.Escribasmostachos.dto.meetups.CreateMeetupDTO;
import com.escribasmostachos.Escribasmostachos.dto.meetups.ReadingMeetupDTO;
import com.escribasmostachos.Escribasmostachos.exception.BusinessLogicalException;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.domain.Pageable;
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
        log.debug("Creating meetup");
        Book book = bookService.getBookById(createMeetupDTO.getBookId());

        if (meetupRepository.existsByCreatorIdAndBookIdAndStatus(creatorId, book.getId(), MeetupStatus.DRAFT)) {
            throw new ResourceAlreadyExistsOnDatabaseException("You already have a draft of a meetup to read that book");
        }
        User creator = new User(creatorId);
        ReadingMeetup newMeetup = new ReadingMeetup(createMeetupDTO.getTitle(), creator, book);
        meetupRepository.save(newMeetup);
        meetupParticipationService.addParticipant(newMeetup, creator);
        return meetupMapper.toReadingMeetupDTO(newMeetup);
    }

    @Transactional(readOnly = true)
    public List<ReadingMeetupDTO> getMeetups(MeetupStatus status, Long creatorId, String usernameToFilter, int limit, int page) {
        log.debug("Getting meetups");
        Pageable pageable = PageRequest.of(page, limit);

        if (creatorId == null && usernameToFilter != null) {
            creatorId = userService.findIdByUsername(usernameToFilter);
            if (creatorId == null) {
                return Collections.emptyList();
            }
        }

        Page<ReadingMeetup> readingMeetupPage = meetupRepository.findWithDetails(status, creatorId, pageable);

        return readingMeetupPage.stream()
            .map(meetup -> meetupMapper.toReadingMeetupDTO(meetup))
            .collect(Collectors.toList());
    }

    @Transactional
    public void joinToMeetup(Long readingMeetupId, Long userId) {
        log.debug("Joining meetup");
        ReadingMeetup readingMeetupToJoin = findReadingMeetupById(readingMeetupId);
        if (!readingMeetupToJoin.getStatus().equals(MeetupStatus.DRAFT)) {
            throw new BusinessLogicalException("You can't join a meetup that is not in DRAFT status");
        }

        boolean alreadyJoined = meetupParticipationService.existsByMeetupIdAndUserId(readingMeetupId, userId);

        if (alreadyJoined) {
            throw new ResourceAlreadyExistsOnDatabaseException("User is already in the meetup");
        }

        User newParticipant = new User(userId);
        meetupParticipationService.addParticipant(readingMeetupToJoin, newParticipant);
    }

    @Transactional
    public void leaveMeetup(Long readingMeetupId, Long userId) {
        log.debug("Leaving meetup");
        ReadingMeetup meetupToLeave = findReadingMeetupById(readingMeetupId);

        if (meetupToLeave.getStatus().equals(MeetupStatus.COMPLETED) || meetupToLeave.getStatus().equals(MeetupStatus.CANCELLED)){
            throw new BusinessLogicalException("Yoy can only leave 'DRAFT' and 'ACTIVE' meetups");
        }
        UserMeetupParticipation participationOpt = meetupParticipationService
            .findByMeetupIdAndUserId(readingMeetupId, userId);

        meetupParticipationService.removeParticipant(participationOpt);
    }

    @Transactional
    public void completeMeetup(Long readingMeetupId, Long userId) {
        log.debug("Marking meetup as completed");

        ReadingMeetup meetupToComplete = findReadingMeetupById(readingMeetupId);

        if (!meetupToComplete.getStatus().equals(MeetupStatus.ACTIVE)){
            throw new BusinessLogicalException("Yoy can only complete 'ACTIVE' meetups");
        }
        meetupParticipationService.markAsCompleted(readingMeetupId, userId);
    }

    @Transactional
    public void deleteMeetup(Long readingMeetupId, Long userId) {
        log.debug("Deleting meetup");
        ReadingMeetup readingMeetupToDelete = findReadingMeetupById(readingMeetupId);
        meetupRepository.deleteById(readingMeetupToDelete.getId());
    }

    @Transactional
    public ReadingMeetupDTO changeMeetupStatus(Long readingMeetupId, Long userId, MeetupStatus status) {
        log.debug("Changing meetup status");
        ReadingMeetup readingMeetupToUpdate = findByIdWithDetails(readingMeetupId);

        if(readingMeetupToUpdate.getCreator().getId() != userId){
            throw new UnauthorizedUserActionException("You can only update status from your own meetups");
        }
        MeetupStatus currentMeetupStatus = readingMeetupToUpdate.getStatus();
        if(currentMeetupStatus.equals(MeetupStatus.CANCELLED)){
            throw new UnauthorizedUserActionException("You can't update meetups canceled");
        }

        if (!currentMeetupStatus.equals(status)) {
            log.debug("Updating meetup with status: " + status);
            switch (status) {
                case COMPLETED:
                    if (!currentMeetupStatus.equals(MeetupStatus.ACTIVE)) {
                        throw new BusinessLogicalException("You can't update meetups to 'COMPLETED' if they weren't in 'ACTIVE' status");
                    }
                    processCompletion(readingMeetupToUpdate);
                    break;
                case ACTIVE:
                    processActivation(readingMeetupToUpdate);
                    break;
                case CANCELLED:
                    processCancellation(readingMeetupToUpdate);
                    break;
                case DRAFT:
                    processBackToDraft(readingMeetupToUpdate);
                    break;
            }
            readingMeetupToUpdate.setStatus(status);
        }

        return meetupMapper.toReadingMeetupDTO(readingMeetupToUpdate);
    }

    /**
     * Processes the completion of a reading meetup.
     * 
     * <p>This operation performs the following steps:
     * <ul>
     *   <li>Retrieves the book associated with the meetup.</li>
     *   <li>Collects all users participating in the meetup.</li>
     *   <li>Fetches existing readings of the book by those users.</li>
     *   <li>Updates the books read count for users who have completed the meetup but do not
     *       yet have a recorded reading of the book.</li>
     *   <li>Creates and saves new {@code UserBookRead} entities for users who completed the meetup
     *       and did not have previous reading records.</li>
     *   <li>Sets the meetup end date to the current date.</li>
     *   <li>Saves the updated users and new reading records to the database.</li>
     * </ul>
     * 
     * <p><b>Important:</b> This method should be called within a transactional context as it performs
     * multiple database write operations that need to be atomic.
     * <br>It is recommended that the public method invoking this one is annotated with {@code @Transactional}.
     * 
     * <p>This business operation is infrequent and only occurs when a reading event (meetup) is completed.
     * 
     * @param meetup the reading meetup to process
     */
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
        meetupParticipationService.removeAllByMeetupId(meetup.getId());
        meetupRepository.save(meetup);
    }

    private void processBackToDraft(ReadingMeetup meetup) {
        meetup.setMeetupStartDate(null);
        meetupRepository.save(meetup);
    }

    public ReadingMeetup findReadingMeetupById(Long readingMeetupId){
        return meetupRepository.findById(readingMeetupId).orElseThrow(() ->new ResourceDontExistsOnDatabaseException("No meeting found")); 
    }

    public ReadingMeetup findByIdWithDetails(Long readingMeetupId){
        return meetupRepository.findByIdWithDetails(readingMeetupId).orElseThrow(() ->new ResourceDontExistsOnDatabaseException("No meeting found")); 
    }
}
