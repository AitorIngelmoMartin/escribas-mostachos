package com.escribasmostachos.Escribasmostachos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escribasmostachos.Escribasmostachos.model.Book;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.model.UserBookRead;

@Repository
public interface UserBookReadRepository extends JpaRepository<UserBookRead, Long>{
    boolean existsByUserAndBook(User user, Book book);
}
