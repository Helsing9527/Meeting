package com.scetop.meeting.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Apply {
    private Integer id;
    private String meetingName;
    private String meetingPlace;
    private Date meetingTime;
    private String meetingDesc;
    private String persons;
    private String status;
}
