package com.escribasmostachos.Escribasmostachos.repository;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.escribasmostachos.Escribasmostachos.model.ReadingMeetup;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.model.UserMeetupParticipation;

public interface UserMeetupParticipationRepository extends JpaRepository<UserMeetupParticipation, Long> {
    boolean existsByMeetupAndUser(ReadingMeetup meetup, User user);

    boolean existsByMeetupIdAndUserId(Long meetupId, Long userId);

    Optional<UserMeetupParticipation> findByMeetupIdAndUserId(Long meetupId, Long userId);
    
    void deleteByMeetupIdAndUserId(Long meetupId, Long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserMeetupParticipation ump WHERE ump.meetup.id = :meetupId")
    void deleteAllByMeetupId(@Param("meetupId") Long meetupId);
}
