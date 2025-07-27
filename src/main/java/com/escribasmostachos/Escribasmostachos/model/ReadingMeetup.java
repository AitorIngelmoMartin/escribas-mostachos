package com.escribasmostachos.Escribasmostachos.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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
import jakarta.persistence.ManyToOne;
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
@EqualsAndHashCode(exclude = {"participants", "creator"})
@Table(name = "reading_meetups")
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

    @OneToMany(mappedBy = "meetup", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserMeetupParticipation> participants = new HashSet<>();

    private LocalDate meetupStartDate;

    private LocalDate meetupEndDate;

    public ReadingMeetup(String title, User creator, Book book) {
        this.title = title;
        this.creator = creator;
        this.book = book;
        this.meetupStartDate = LocalDate.now();
        this.setStatus(MeetupStatus.DRAFT);
    }
}
