package com.escribasmostachos.Escribasmostachos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.escribasmostachos.Escribasmostachos.exception.BusinessConflictException;
import com.escribasmostachos.Escribasmostachos.exception.ResourceDontExistsOnDatabaseException;
import com.escribasmostachos.Escribasmostachos.model.ReadingMeetup;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.model.UserMeetupParticipation;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MeetupParticipationService {

    @Transactional
    public void addParticipant(ReadingMeetup meetup, User user) {
        boolean alreadyExists = meetup.getParticipants().stream()
                .anyMatch(participant -> participant.getUser().getId().equals(user.getId()));

        if (alreadyExists) {
            throw new BusinessConflictException("The user is already a participant in the meetup");
        }

        UserMeetupParticipation participation = new UserMeetupParticipation();
        participation.setUser(user);
        participation.setMeetup(meetup);
        participation.setCompleted(false);

        meetup.getParticipants().add(participation);
        user.getMeetupParticipations().add(participation);
    }

    @Transactional
    public void removeParticipant(ReadingMeetup meetup, User user) {
        boolean removed = meetup.getParticipants().removeIf(participant -> participant.getUser().equals(user));
        if (removed) {
            user.getMeetupParticipations().removeIf(participant -> participant.getMeetup().equals(meetup));
        }
    }

    @Transactional
    public void markAsCompleted(ReadingMeetup meetup, User user) {
        meetup.getParticipants().stream()
            .filter(participant -> participant.getUser().equals(user))
            .findFirst()
            .orElseThrow(() -> new ResourceDontExistsOnDatabaseException("Not a participant"))
            .setCompleted(true);
    }
}