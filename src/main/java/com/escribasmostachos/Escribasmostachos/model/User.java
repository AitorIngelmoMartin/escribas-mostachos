package com.escribasmostachos.Escribasmostachos.model;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.escribasmostachos.Escribasmostachos.dto.auth.RegisterRequestDTO;
import com.escribasmostachos.Escribasmostachos.dto.books.BookDTO;
import com.escribasmostachos.Escribasmostachos.dto.profiles.PrivateUserProfileDTO;
import com.escribasmostachos.Escribasmostachos.dto.profiles.ProfileUpdateDTO;
import com.escribasmostachos.Escribasmostachos.dto.profiles.UserProfileDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"readingMeetups", "createdMeetups", "currentBooks"})
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

    @Enumerated(EnumType.STRING)
    private RoleType role;

    // Profile fields
    @Column(length = 25, nullable = true)
    private String firstName;

    @Column(length = 25, nullable = true)
    private String lastName;

    private String profilePictureUrl;

    private Integer booksReadCount = 0;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserBookRead> currentBooks;

    @ManyToMany(mappedBy = "participants")
    private Set<ReadingMeetup> readingMeetups = new HashSet<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReadingMeetup> createdMeetups = new ArrayList<>();

    private LocalDate membershipDate;

    private Boolean profileIsPrivate = false;

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

        if (this.currentBooks != null) {
            List<BookDTO> currentBookDTOs = this.currentBooks.stream()
                .filter(read -> read.getStatus() == ReadStatus.R)
                .map(read -> {
                    Book book = read.getBook();
                    return book.toBookDto();
                })
                .collect(Collectors.toList());
            dto.setCurrentBooks(currentBookDTOs);
        }

        dto.setMembershipDate(this.membershipDate);
        return dto;
    }

    public PrivateUserProfileDTO toPrivateUserProfileDTO() {
        PrivateUserProfileDTO publicDTO = new PrivateUserProfileDTO();
        publicDTO.setUsername(this.username);
        publicDTO.setProfilePictureUrl(this.profilePictureUrl);
        return publicDTO;
    }

    public void addCurrentBookRead(UserBookRead reading) {
        this.currentBooks.add(reading);
        reading.setUser(this);
    }

    public void removeCurrentBookRead(UserBookRead reading) {
        this.currentBooks.remove(reading);
    }

    public void addCreatedMeetup(ReadingMeetup meetup) {
        this.createdMeetups.add(meetup);
        meetup.setCreator(this);
    }

    public void removeCreatedMeetup(ReadingMeetup meetup) {
        this.createdMeetups.remove(meetup);
        meetup.setCreator(null);
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

        if (dto.getProfileIsPrivate() != null && !Objects.equals(this.profileIsPrivate, dto.getProfileIsPrivate())) {
            this.profileIsPrivate = dto.getProfileIsPrivate();
        }
    }
}