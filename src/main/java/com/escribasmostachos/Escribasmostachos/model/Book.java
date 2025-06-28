package com.escribasmostachos.Escribasmostachos.model;

import com.escribasmostachos.Escribasmostachos.dto.BookDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String isbn;
    
    private String title;
    private String author;
    private String coverUrl;
    private String updatedBy;

    public Book fromDto(BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        return book;
    }

    public BookDTO toBookDto() {
        BookDTO dto = new BookDTO();
        dto.setTitle(this.title);
        dto.setAuthor(this.author);
        dto.setCoverUrl(this.coverUrl);
        dto.setUpdatedBy(this.updatedBy);

        return dto;
    }
}