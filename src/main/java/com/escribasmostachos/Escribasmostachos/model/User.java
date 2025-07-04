package com.escribasmostachos.Escribasmostachos.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.escribasmostachos.Escribasmostachos.dto.BookDTO;
import com.escribasmostachos.Escribasmostachos.dto.ProfileUpdateDTO;
import com.escribasmostachos.Escribasmostachos.dto.RegisterRequestDTO;
import com.escribasmostachos.Escribasmostachos.dto.UserProfileDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    @Column(length = 25, nullable = true)
    private String firstName;

    @Column(length = 25, nullable = true)
    private String lastName;

    private String profilePictureUrl;

    private Integer booksReadCount = 0;

    @OneToOne(optional = true)
    private Book currentBook;

    private LocalDate membershipDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<UserBookRead> booksRead = new java.util.ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    public static User fromDto(RegisterRequestDTO dto, String encodedPassword) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setPassword(encodedPassword);
        return user;
    }

    public UserProfileDTO toProfileDto() {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername(this.username);
        dto.setFirstName(this.firstName);
        dto.setLastName(this.lastName);
        dto.setProfilePictureUrl(this.profilePictureUrl);
        dto.setBooksReadCount(this.booksReadCount);

        if (this.currentBook != null) {
            BookDTO bookDTO = new BookDTO();
            bookDTO.setTitle(this.currentBook.getTitle());
            bookDTO.setAuthor(this.currentBook.getAuthor());
            bookDTO.setCoverUrl(this.currentBook.getCoverUrl());
            dto.setCurrentBook(bookDTO);
        }

        dto.setMembershipDate(this.membershipDate);
        return dto;
    }

    public void updatePropertiesFromDto(ProfileUpdateDTO dto) {
        if (dto.getFirstName() != null && !Objects.equals(this.firstName, dto.getFirstName())) {
            this.firstName = dto.getFirstName();
        }

        if (dto.getLastName() != null && !Objects.equals(this.lastName, dto.getLastName())) {
            this.lastName = dto.getLastName();
        }

        if (dto.getProfilePictureUrl() != null && !Objects.equals(this.profilePictureUrl, dto.getProfilePictureUrl())) {
            this.profilePictureUrl = dto.getProfilePictureUrl();
        }
    }
}