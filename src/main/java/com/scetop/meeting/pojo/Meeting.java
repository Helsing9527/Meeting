package com.scetop.meeting.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meeting {
    private Integer id;
    private String meetingName;
    private String meetingRoom;
    private String startingTime;
    private String endTime;
    private Long timestamp;
    private String meetingDesc;
    private String sponsor;
    private Integer sponsorId;
    private String status;

    @TableField(exist = false)
    private String[] participants;
    @TableField(exist = false)
    private String checkInStatus;
}
