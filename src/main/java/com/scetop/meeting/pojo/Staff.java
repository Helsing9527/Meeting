package com.scetop.meeting.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Staff {
    private Integer id;
    private String userName;
    private String dept;
    private String post;
    private String gender;
    private String admin;

    @TableField(exist = false)
    private String imgBase64;
    @TableField(exist = false)
    private String status;
}
