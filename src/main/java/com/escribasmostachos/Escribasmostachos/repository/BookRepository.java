package com.escribasmostachos.Escribasmostachos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escribasmostachos.Escribasmostachos.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{
    
}
