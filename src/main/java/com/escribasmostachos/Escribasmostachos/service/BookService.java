package com.escribasmostachos.Escribasmostachos.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
}
