package com.escribasmostachos.Escribasmostachos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.escribasmostachos.Escribasmostachos.dto.BookDTO;
import com.escribasmostachos.Escribasmostachos.dto.RegisterRequestDto;
import com.escribasmostachos.Escribasmostachos.dto.UserProfileDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 20, unique = true, nullable = false)
    private String username;

    private String password;

    // Profile fields
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    // private List<String> favoriteGenres;
    private Integer booksReadCount;

    @OneToOne
    private Book currentBook;

    private LocalDate membershipDate;
    // private List<String> roles;
    private LocalDateTime lastActive;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    public static User fromDto(RegisterRequestDto dto, String encodedPassword) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(encodedPassword);
        return user;
    }

    public UserProfileDto toProfileDto() {
        UserProfileDto dto = new UserProfileDto();
        dto.setUsername(this.username);
        dto.setFirstName(this.firstName);
        dto.setLastName(this.lastName);
        dto.setProfilePictureUrl(this.profilePictureUrl);
        // dto.setFavoriteGenres(this.favoriteGenres);
        dto.setBooksReadCount(this.booksReadCount);

        if (this.currentBook != null) {
            BookDTO bookDTO = new BookDTO();
            bookDTO.setTitle(this.currentBook.getTitle());
            bookDTO.setAuthor(this.currentBook.getAuthor());
            bookDTO.setCoverUrl(this.currentBook.getCoverUrl());
            dto.setCurrentBook(bookDTO);
        }

        dto.setMembershipDate(this.membershipDate);
        // dto.setRoles(this.roles);
        dto.setLastActive(this.lastActive);

        return dto;
    }
}