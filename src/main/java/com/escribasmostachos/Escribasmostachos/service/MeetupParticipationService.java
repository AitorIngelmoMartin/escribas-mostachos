package com.escribasmostachos.Escribasmostachos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.escribasmostachos.Escribasmostachos.exception.BusinessLogicalException;
import com.escribasmostachos.Escribasmostachos.exception.ResourceDontExistsOnDatabaseException;
import com.escribasmostachos.Escribasmostachos.model.ReadingMeetup;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.model.UserMeetupParticipation;
import com.escribasmostachos.Escribasmostachos.repository.UserMeetupParticipationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MeetupParticipationService {
    
    private final UserMeetupParticipationRepository userMeetupParticipationRepository;

    public MeetupParticipationService(UserMeetupParticipationRepository userMeetupParticipationRepository){
        this.userMeetupParticipationRepository = userMeetupParticipationRepository;
    }

    @Transactional
    public void addParticipant(ReadingMeetup meetup, User user) {
        boolean alreadyExists = userMeetupParticipationRepository.existsByMeetupAndUser(meetup, user);
        if (alreadyExists) {
            throw new BusinessLogicalException("The user is already a participant in the meetup");
        }

        UserMeetupParticipation participation = new UserMeetupParticipation();
        participation.setUser(user);
        participation.setMeetup(meetup);
        participation.setCompleted(false);

        userMeetupParticipationRepository.save(participation);

        meetup.getParticipants().add(participation);
        user.getMeetupParticipations().add(participation);
    }

    public void removeParticipant(UserMeetupParticipation meetupParticipation){
        userMeetupParticipationRepository.delete(meetupParticipation);
    }

    @Transactional
    public void markAsCompleted(Long meetupId, Long userId) {
        UserMeetupParticipation participation = userMeetupParticipationRepository
            .findByMeetupIdAndUserId(meetupId, userId)
            .orElseThrow(() -> new ResourceDontExistsOnDatabaseException("Not a participant"));
        
        participation.setCompleted(true);
    }

    public boolean existsByMeetupIdAndUserId(Long readingMeetupId,Long userId){
        return userMeetupParticipationRepository.existsByMeetupIdAndUserId(readingMeetupId, userId);
    }

    public UserMeetupParticipation findByMeetupIdAndUserId(Long readingMeetupId,Long userId){
        return userMeetupParticipationRepository
                    .findByMeetupIdAndUserId(readingMeetupId, userId)
                    .orElseThrow(() -> new ResourceDontExistsOnDatabaseException("User not registered in that meetup"));
    }

    @Transactional
    public void removeAllByMeetupId(Long meetupId) {
        userMeetupParticipationRepository.deleteAllByMeetupId(meetupId);
    }

}