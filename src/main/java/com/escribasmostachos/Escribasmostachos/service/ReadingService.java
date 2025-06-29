package com.escribasmostachos.Escribasmostachos.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.escribasmostachos.Escribasmostachos.dto.BookReadingDto;
import com.escribasmostachos.Escribasmostachos.exception.BookDontExistsException;
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
    public boolean markBookAsRead(BookReadingDto dto, String username) {
        log.info("Finding user with username: " + username);
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent())  {
            throw new UsernameNotFoundException("User not found");
        }

        Optional<Book> book = bookRepository.findByIsbn(dto.getIsbn());
        if (!book.isPresent())  {
            throw new BookDontExistsException("No book found with ISBN: " + dto.getIsbn());
        }

        User userThatReadTheBook = user.get();
        Book bookReadByUser = book.get();

        boolean bookAlreadyRegistered = userBookReadRepository.existsByUserAndBook(userThatReadTheBook, bookReadByUser);

        if(bookAlreadyRegistered){
            return false;
        }

        UserBookRead userRead = new UserBookRead(userThatReadTheBook, bookReadByUser);

        userBookReadRepository.save(userRead);
        userThatReadTheBook.setBooksReadCount(userThatReadTheBook.getBooksReadCount() + 1);
        return true;
    }
}
