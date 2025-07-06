package com.escribasmostachos.Escribasmostachos.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escribasmostachos.Escribasmostachos.model.MeetupStatus;
import com.escribasmostachos.Escribasmostachos.model.ReadingMeetup;

@Repository
public interface ReadingMeetupRepository extends JpaRepository<ReadingMeetup, Long>{
    Page<ReadingMeetup> findAllByStatus(MeetupStatus status, Pageable pageable);
    Page<ReadingMeetup> findAllByCreatorId(Long creatorId, Pageable pageable);
    Page<ReadingMeetup> findAllByStatusAndCreatorId(MeetupStatus status, Long creatorId, Pageable pageable);
}
