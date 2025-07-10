package com.escribasmostachos.Escribasmostachos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escribasmostachos.Escribasmostachos.model.Book;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{
    Optional<Book> findByIsbn(String isbn);
}
