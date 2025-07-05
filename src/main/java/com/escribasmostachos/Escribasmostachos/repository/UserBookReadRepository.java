package com.escribasmostachos.Escribasmostachos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.escribasmostachos.Escribasmostachos.model.Book;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.model.UserBookRead;

@Repository
public interface UserBookReadRepository extends JpaRepository<UserBookRead, Long>{
    boolean existsByUserAndBook(User user, Book book);
    
    Optional<UserBookRead> findByUserIdAndBookId(Long userId, Long bookId);

    List<UserBookRead> findByUserId(Long userId);

    Page<UserBookRead> findByUserId(Long userId, Pageable pageable);
}
