package com.scetop.meeting.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
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
    private String startTime;
    private String endTime;
    private String meetingDesc;
    @TableField(exist = false)
    private String[] persons;
    private String initiator;
    private String status;
}
