package com.escribasmostachos.Escribasmostachos.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.escribasmostachos.Escribasmostachos.dto.BookDTO;
import com.escribasmostachos.Escribasmostachos.dto.BookReadingDTO;
import com.escribasmostachos.Escribasmostachos.exception.ResourceAlreadyExistsOnDatabaseException;
import com.escribasmostachos.Escribasmostachos.exception.ResourceDontExistsOnDatabaseException;
import com.escribasmostachos.Escribasmostachos.model.Book;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.model.UserBookRead;
import com.escribasmostachos.Escribasmostachos.repository.BookRepository;
import com.escribasmostachos.Escribasmostachos.repository.UserBookReadRepository;
import com.escribasmostachos.Escribasmostachos.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReadingService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserBookReadRepository userBookReadRepository;

    public ReadingService(UserRepository userRepository, BookRepository bookRepository, UserBookReadRepository userBookReadRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.userBookReadRepository = userBookReadRepository;
    }

    @Transactional
    public void markBookAsRead(BookReadingDTO dto, Long userId) {
        log.info("Finding user with userId: " + userId);
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent())  {
            throw new UsernameNotFoundException("User not found");
        }

        Optional<Book> book = bookRepository.findByIsbn(dto.getIsbn());
        if (!book.isPresent())  {
            throw new ResourceDontExistsOnDatabaseException("No book found on database with ISBN: " + dto.getIsbn());
        }

        User userThatReadTheBook = user.get();
        Book bookReadByUser = book.get();

        boolean bookAlreadyRegistered = userBookReadRepository.existsByUserAndBook(userThatReadTheBook, bookReadByUser);

        if(bookAlreadyRegistered){
            throw new ResourceAlreadyExistsOnDatabaseException("User already registered this read");
        }

        UserBookRead userRead = new UserBookRead(userThatReadTheBook, bookReadByUser);
        userRead.markBookAsRead();
        
        userBookReadRepository.save(userRead);
        userThatReadTheBook.setBooksReadCount(userThatReadTheBook.getBooksReadCount() + 1);
    }
    
    @Transactional(readOnly = true)
    public List<BookDTO> getBooksReadByUser(Long userId) {
        log.info("Getting reads for user with id: " + userId);
        List<UserBookRead> userReads = userBookReadRepository.findByUserId(userId);
        return userReads.stream()
            .map(userBookRead -> userBookRead.getBook().toBookDto())
            .collect(Collectors.toList());
    }
}
