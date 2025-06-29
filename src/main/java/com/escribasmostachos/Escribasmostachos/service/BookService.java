package com.escribasmostachos.Escribasmostachos.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.escribasmostachos.Escribasmostachos.dto.BookDTO;
import com.escribasmostachos.Escribasmostachos.dto.BookUpdateDTO;
import com.escribasmostachos.Escribasmostachos.exception.BookAlreadyExistsException;
import com.escribasmostachos.Escribasmostachos.exception.BookDontExistsException;
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

    @Transactional
    public boolean updateBookFromDatabase(BookUpdateDTO bookUpdateDto, String username) {
        Optional<Book> bookReadedFromDatabase = bookRepository.findByIsbn(bookUpdateDto.getOldIsbn());
        if (!bookReadedFromDatabase.isPresent()) {
            throw new BookDontExistsException("A book with ISBN: " + bookUpdateDto.getOldIsbn() + " don't exists");
        }
        
        Book oldBookInfo = bookReadedFromDatabase.get();
        if (!haveSomethingToUpdate(bookUpdateDto, oldBookInfo)){
            return false;
        }

        oldBookInfo.updatePropertiesFromDto(bookUpdateDto);
        oldBookInfo.setUpdatedBy(username);
        return true;
    }

    public boolean haveSomethingToUpdate(BookUpdateDTO dto, Book currentBook) {
        return (dto.getNewIsbn() != null && !Objects.equals(currentBook.getIsbn(), dto.getNewIsbn())) ||
            (dto.getTitle() != null && !Objects.equals(currentBook.getTitle(), dto.getTitle())) ||
            (dto.getAuthor() != null && !Objects.equals(currentBook.getAuthor(), dto.getAuthor())) ||
            (dto.getCoverUrl() != null && !Objects.equals(currentBook.getCoverUrl(), dto.getCoverUrl()));
    }
}

