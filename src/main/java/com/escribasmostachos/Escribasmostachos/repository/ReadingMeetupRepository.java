package com.escribasmostachos.Escribasmostachos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.escribasmostachos.Escribasmostachos.model.MeetupStatus;
import com.escribasmostachos.Escribasmostachos.model.ReadingMeetup;

import java.util.Optional;

@Repository
public interface ReadingMeetupRepository extends JpaRepository<ReadingMeetup, Long>{
    Page<ReadingMeetup> findAllByStatus(MeetupStatus status, Pageable pageable);
    Page<ReadingMeetup> findAllByCreatorId(Long creatorId, Pageable pageable);
    Page<ReadingMeetup> findAllByStatusAndCreatorId(MeetupStatus status, Long creatorId, Pageable pageable);
    boolean existsByCreatorIdAndBookIdAndStatus(Long creatorId, Long bookId, MeetupStatus status);

    @Query("""
    SELECT rm FROM ReadingMeetup rm
    JOIN FETCH rm.creator
    JOIN FETCH rm.book
    LEFT JOIN FETCH rm.participants
    WHERE rm.id = :id
    """)
    Optional<ReadingMeetup> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT rm FROM ReadingMeetup rm
        JOIN FETCH rm.creator
        JOIN FETCH rm.book
        WHERE (:status IS NULL OR rm.status = :status)
        AND (:creatorId IS NULL OR rm.creator.id = :creatorId)
    """)
    Page<ReadingMeetup> findWithDetails(
        @Param("status") MeetupStatus status,
        @Param("creatorId") Long creatorId,
        Pageable pageable);
}
