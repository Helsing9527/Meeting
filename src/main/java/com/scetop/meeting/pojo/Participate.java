package com.scetop.meeting.pojo;

import lombok.Data;

@Data
public class Participate {
    private Integer id;
    private Integer apply_id;
    private Integer user_id;
    private String status;
}
