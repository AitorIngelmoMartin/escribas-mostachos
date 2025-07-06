package com.escribasmostachos.Escribasmostachos.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.escribasmostachos.Escribasmostachos.dto.ReadingMeetupDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"participants", "creator"})
@Table(name = "reading_meetups", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_CreatorBookActiveMeetup", columnNames = {"creator_user_id", "book_id", "status"})
})
public class ReadingMeetup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_user_id", nullable = false)
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MeetupStatus status = MeetupStatus.DRAFT;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "meetup_participants",
        joinColumns = @JoinColumn(name = "meetup_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();

    private LocalDate meetupStartDate;

    private LocalDate meetupEndDate;

    public void addParticipant(User user) {
        this.participants.add(user);
        user.getReadingMeetups().add(this);
    }

    public void removeParticipant(User user) {
        this.participants.remove(user);
        user.getReadingMeetups().remove(this);
    }

    public ReadingMeetup(String title, User creator, Book book) {
        this.title = title;
        this.creator = creator;
        this.book = book;
        this.setStatus(MeetupStatus.DRAFT);
    }

    public ReadingMeetupDTO toReadingMeetupDTO(){
        ReadingMeetupDTO readingMeetupDTO = new ReadingMeetupDTO();
        readingMeetupDTO.setTitle(this.title);
        readingMeetupDTO.setCreator(this.creator.getUsername());
        readingMeetupDTO.setBook(this.book.toBookDto());
        readingMeetupDTO.setStatus(this.status);
        readingMeetupDTO.setParticipants(this.participants.stream()
                                            .map(User::getUsername)
                                            .collect(Collectors.toSet()));

        
        if (this.meetupStartDate != null){
            readingMeetupDTO.setMeetupStartDate(meetupStartDate);
        }

        if (this.meetupEndDate != null){
            readingMeetupDTO.setMeetupEndDate(this.meetupEndDate);
        }

        return readingMeetupDTO;
    }
}
