package com.escribasmostachos.Escribasmostachos.model;

import com.escribasmostachos.Escribasmostachos.dto.BookDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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

    private String title;
    private String author;
    private String coverUrl;

    @ManyToOne
    private User addedBy;


    public BookDTO toBookDto() {
        BookDTO dto = new BookDTO();
        dto.setTitle(this.title);
        dto.setAuthor(this.author);
        dto.setCoverUrl(this.coverUrl);
        dto.setAddedBy(this.addedBy.toProfileDto());

        return dto;
    }
}