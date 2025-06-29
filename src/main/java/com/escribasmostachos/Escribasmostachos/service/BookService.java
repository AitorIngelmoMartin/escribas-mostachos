package com.escribasmostachos.Escribasmostachos.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.escribasmostachos.Escribasmostachos.dto.BookDTO;
import com.escribasmostachos.Escribasmostachos.exception.BookAlreadyExistsException;
import com.escribasmostachos.Escribasmostachos.model.Book;
import com.escribasmostachos.Escribasmostachos.repository.BookRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    public List<Book> getBooks(int limit, int page) {
        PageRequest pageable = PageRequest.of(page, limit);
        return bookRepository.findAll(pageable).getContent();
    }

    public Book addBookToDatabase(BookDTO bookDto, String username) {
        if (bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
            throw new BookAlreadyExistsException("A book with ISBN: " + bookDto.getIsbn() + " already exists");
        }

        Book newBook = new Book().fromDto(bookDto);
        newBook.setUpdatedBy(username);
        return bookRepository.save(newBook);
    }
}
