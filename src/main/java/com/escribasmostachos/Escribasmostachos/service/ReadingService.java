package com.escribasmostachos.Escribasmostachos.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.escribasmostachos.Escribasmostachos.dto.BookDTO;
import com.escribasmostachos.Escribasmostachos.dto.BookReadingDTO;
import com.escribasmostachos.Escribasmostachos.exception.MaxCurrentBooksReachedException;
import com.escribasmostachos.Escribasmostachos.exception.ResourceAlreadyExistsOnDatabaseException;
import com.escribasmostachos.Escribasmostachos.model.Book;
import com.escribasmostachos.Escribasmostachos.model.ReadStatus;
import com.escribasmostachos.Escribasmostachos.model.User;
import com.escribasmostachos.Escribasmostachos.model.UserBookRead;
import com.escribasmostachos.Escribasmostachos.repository.UserBookReadRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReadingService {

    @Value("${escribasmostachos.configuration.max-number-of-current-reads}")
    private Integer MAX_NUMBER_OF_CURRENT_READS;

    private final UserService userService;
    private final BookService bookService;
    private final UserBookReadRepository userBookReadRepository;

    public ReadingService(UserService userService, BookService bookService, UserBookReadRepository userBookReadRepository) {
        this.userService = userService;
        this.bookService = bookService;
        this.userBookReadRepository = userBookReadRepository;
    }

    @Transactional
    public void addCurrentBook(BookReadingDTO dto, Long userId) {
        User userToAddReading = userService.getUserById(userId);
        Book bookToAdd = bookService.getBookByIsbn(dto.getIsbn());
   
        boolean bookAlreadyRegistered = userBookReadRepository.existsByUserAndBook(userToAddReading, bookToAdd);

        if(bookAlreadyRegistered){
            throw new ResourceAlreadyExistsOnDatabaseException("User already registered this read");
        }
        long userCurrentReadingCount = userToAddReading.getCurrentBooks().stream()
            .filter(read -> read.getStatus() == ReadStatus.R)
            .count();

        if (userCurrentReadingCount >= MAX_NUMBER_OF_CURRENT_READS) {
            throw new MaxCurrentBooksReachedException();
        }

        UserBookRead newCurrentReading = new UserBookRead(userToAddReading, bookToAdd);
        newCurrentReading.markBookAsReading();
        userBookReadRepository.save(newCurrentReading);

        userToAddReading.addCurrentBookRead(newCurrentReading);
        userService.save(userToAddReading);
    }

    @Transactional
    public void markBookAsRead(BookReadingDTO dto, Long userId) {
        User userThatReadTheBook = userService.getUserById(userId);
        Book bookReadByUser = bookService.getBookByIsbn(dto.getIsbn());
        log.debug("UserId: " + userThatReadTheBook.getId());
        log.debug("BookId: " + bookReadByUser.getId());
        Optional<UserBookRead> bookReadingAlreadyRegistered = userBookReadRepository.findByUserIdAndBookId(userThatReadTheBook.getId(), bookReadByUser.getId());

        UserBookRead userBookRead;
        if(bookReadingAlreadyRegistered.isPresent()){
            userBookRead = bookReadingAlreadyRegistered.get();
            log.debug("Read already saved on DDBB");
            if (userBookRead.getStatus() == ReadStatus.F){
                throw new ResourceAlreadyExistsOnDatabaseException("User already registered this read");
            }
        }else{
            log.debug("Creating new book read registry");
            userBookRead = new UserBookRead(userThatReadTheBook, bookReadByUser);
        }
        userBookRead.markBookAsRead();
        userBookReadRepository.save(userBookRead);
        updateUserReadBookInfo(userThatReadTheBook, bookReadByUser, userBookRead);
    }
    
    private void updateUserReadBookInfo(User user, Book book, UserBookRead userBookRead){
        boolean isInCurrentBooks = user.getCurrentBooks().stream()
                                        .anyMatch(reading -> reading.getBook()
                                                                    .getId()
                                                                    .equals(book.getId()));

        if(isInCurrentBooks){
            log.debug("Book was on current books read. Deleting it from the list");
            user.removeCurrentBookRead(userBookRead);
        }
        user.setBooksReadCount(user.getBooksReadCount() + 1);
    }

    @Transactional(readOnly = true)
    public List<BookDTO> getBooksReadByUser(Long userId, int limit, int page) {
        log.info("Getting " +limit+ " reads from page " + page + " for user with id: " + userId);
        PageRequest pageable = PageRequest.of(page, limit);
        List<UserBookRead> userReads = userBookReadRepository.findByUserId(userId, pageable).getContent();
        return userReads.stream()
            .map(userBookRead -> userBookRead.getBook().toBookDto())
            .collect(Collectors.toList());
    }
}
