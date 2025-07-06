package com.escribasmostachos.Escribasmostachos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escribasmostachos.Escribasmostachos.model.ReadingMeetup;

@Repository
public interface ReadingMeetupRepository extends JpaRepository<ReadingMeetup, Long>{
    
}
