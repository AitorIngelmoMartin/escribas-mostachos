package com.escribasmostachos.Escribasmostachos.service.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.escribasmostachos.Escribasmostachos.dto.meetups.ReadingMeetupDTO;
import com.escribasmostachos.Escribasmostachos.model.ReadingMeetup;
import com.escribasmostachos.Escribasmostachos.utils.PublicIdGenerator;

@Component
public class ReadingMeetupMapper {

    private final PublicIdGenerator publicIdGenerator;

    public ReadingMeetupMapper(PublicIdGenerator publicIdGenerator) {
        this.publicIdGenerator = publicIdGenerator;
    }

    public ReadingMeetupDTO toReadingMeetupDTO(ReadingMeetup meetup) {
        ReadingMeetupDTO dto = new ReadingMeetupDTO();
        dto.setTitle(meetup.getTitle());
        dto.setCreator(meetup.getCreator().getUsername());
        dto.setBook(meetup.getBook().toBookDto());
        dto.setStatus(meetup.getStatus());
        dto.setParticipants(meetup.getParticipants().stream()
                          .map(participation -> participation.getUser().getUsername())
                          .collect(Collectors.toSet()));
        dto.setPublicId(publicIdGenerator.encode(meetup.getId()));

        if (meetup.getMeetupStartDate() != null)
            dto.setMeetupStartDate(meetup.getMeetupStartDate());

        if (meetup.getMeetupEndDate() != null)
            dto.setMeetupEndDate(meetup.getMeetupEndDate());

        return dto;
    }
}