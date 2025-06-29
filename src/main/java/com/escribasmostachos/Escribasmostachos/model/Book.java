package com.escribasmostachos.Escribasmostachos.model;

import com.escribasmostachos.Escribasmostachos.dto.BookDTO;
import com.escribasmostachos.Escribasmostachos.dto.BookUpdateDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String isbn;
    
    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;
    
    private String coverUrl;
    private String updatedBy;

    public Book fromDto(BookDTO dto) {
        Book book = new Book();
        book.setIsbn(dto.getIsbn());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setCoverUrl(dto.getCoverUrl());
        return book;
    }

    public void updatePropertiesFromDto(BookUpdateDTO dto) {
        if (dto.getNewIsbn() != null && !Objects.equals(this.isbn, dto.getNewIsbn())) {
            this.isbn = dto.getNewIsbn();
        }

        if (dto.getTitle() != null && !Objects.equals(this.title, dto.getTitle())) {
            this.title = dto.getTitle();
        }

        if (dto.getAuthor() != null && !Objects.equals(this.author, dto.getAuthor())) {
            this.author = dto.getAuthor();
        }

        if (dto.getCoverUrl() != null && !Objects.equals(this.coverUrl, dto.getCoverUrl())) {
            this.coverUrl = dto.getCoverUrl();
        }
    }

    public BookDTO toBookDto() {
        BookDTO dto = new BookDTO();
        dto.setIsbn(this.isbn);
        dto.setTitle(this.title);
        dto.setAuthor(this.author);
        dto.setCoverUrl(this.coverUrl);
        return dto;
    }
}