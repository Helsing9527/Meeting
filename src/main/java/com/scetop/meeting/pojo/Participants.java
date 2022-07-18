package com.scetop.meeting.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participants {
    private Integer id;
    private Integer meetingId;
    private Integer staffId;
    private String checkInStatus;

    @TableField(exist = false)
    private Integer total;
    @TableField(exist = false)
    private Integer checkIn;
    @TableField(exist = false)
    private Integer notCheckIn;
}
