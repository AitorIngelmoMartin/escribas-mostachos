package com.escribasmostachos.Escribasmostachos.dto.meetups;

import com.escribasmostachos.Escribasmostachos.model.MeetupStatus;

import lombok.Data;

@Data
public class UpdateMeetupStatusDTO {
    
    private MeetupStatus status;

}
