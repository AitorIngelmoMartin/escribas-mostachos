package com.escribasmostachos.Escribasmostachos.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.ISBNValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.escribasmostachos.Escribasmostachos.dto.BookDTO;
import com.escribasmostachos.Escribasmostachos.dto.BookUpdateDTO;
import com.escribasmostachos.Escribasmostachos.exception.InvalidIsbnException;
import com.escribasmostachos.Escribasmostachos.exception.ResourceAlreadyExistsOnDatabaseException;
import com.escribasmostachos.Escribasmostachos.exception.ResourceDontExistsOnDatabaseException;
import com.escribasmostachos.Escribasmostachos.model.Book;
import com.escribasmostachos.Escribasmostachos.repository.BookRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookService {

    private ISBNValidator validator = new ISBNValidator();

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    public List<BookDTO> getBooks(int limit, int page) {
        PageRequest pageable = PageRequest.of(page, limit);
        List<Book> books = bookRepository.findAll(pageable).getContent();

        return books.stream()
                    .map(Book::toBookDto)
                    .collect(Collectors.toList());
    }

    public BookDTO addBookToDatabase(BookDTO bookDto, String username) {
        if (bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
            throw new ResourceAlreadyExistsOnDatabaseException("A book with ISBN: " + bookDto.getIsbn() + " already exists");
        }

        Book newBook = new Book().fromDto(bookDto);
        newBook.setUpdatedBy(username);
        bookRepository.save(newBook);
        return newBook.toBookDto();
    }

    @Transactional
    public boolean updateBookFromDatabase(BookUpdateDTO bookUpdateDto, String username) {
        if (!haveSomethingToUpdate(bookUpdateDto)){
            return false;
        }

        if(bookUpdateDto.getNewIsbn() != null && !validator.isValid(bookUpdateDto.getNewIsbn())){
            log.error("Error trying to validate ISBN from DTO: " + bookUpdateDto);
            throw new InvalidIsbnException(bookUpdateDto.getNewIsbn() + " is an invalid ISBN");
        }

        Book oldBookInfo = bookRepository.findByIsbn(bookUpdateDto.getBookIsbn())
                                        .orElseThrow(() -> new ResourceDontExistsOnDatabaseException(
                                            "A book with ISBN: " + bookUpdateDto.getBookIsbn() + " don't exists"
                                        ));
        oldBookInfo.updatePropertiesFromDto(bookUpdateDto);
        oldBookInfo.setUpdatedBy(username);
        return true;
    }

    private boolean haveSomethingToUpdate(BookUpdateDTO dto) {
        return (dto.getNewIsbn() != null) ||
            (dto.getTitle() != null) ||
            (dto.getAuthor() != null) ||
            (dto.getCoverUrl() != null);
    }

    public Book getBookByIsbn(String isbn){
        return bookRepository.findByIsbn(isbn)
                         .orElseThrow(() -> new ResourceDontExistsOnDatabaseException("No book found on database with ISBN: " + isbn));
    }
    
    public BookDTO getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                         .map(Book::toBookDto)
                         .orElseThrow(() -> new ResourceDontExistsOnDatabaseException("No book found on database with ID: " + bookId));
    }

    public void save(Book book) {
        bookRepository.save(book);
    }
}

