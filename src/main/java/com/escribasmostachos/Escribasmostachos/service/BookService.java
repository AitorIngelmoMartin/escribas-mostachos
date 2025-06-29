package com.escribasmostachos.Escribasmostachos.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.escribasmostachos.Escribasmostachos.dto.BookDTO;
import com.escribasmostachos.Escribasmostachos.exception.BookAlreadyExistsException;
import com.escribasmostachos.Escribasmostachos.exception.InvalidIsbnException;
import com.escribasmostachos.Escribasmostachos.model.Book;
import com.escribasmostachos.Escribasmostachos.repository.BookRepository;

import org.apache.commons.validator.routines.ISBNValidator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookService {

    private final BookRepository bookRepository;
    private ISBNValidator isbnValidator = new ISBNValidator();

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    public List<Book> getBooks(int limit, int page) {
        PageRequest pageable = PageRequest.of(page, limit);
        return bookRepository.findAll(pageable).getContent();
    }

    public Book addBookToDatabase(BookDTO bookDto, String username) {
        if (!isbnValidator.isValid(bookDto.getIsbn().replaceAll("[-\\s]", ""))) {
            log.error("Error trying to validate ISBN from DTO: " + bookDto);
            throw new InvalidIsbnException(bookDto.getIsbn() + " is an invalid ISBN");
        }

        if (bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
            throw new BookAlreadyExistsException("A book with ISBN: " + bookDto.getIsbn() + " already exists");
        }

        Book newBook = new Book().fromDto(bookDto);
        newBook.setUpdatedBy(username);
        return bookRepository.save(newBook);
    }
}
