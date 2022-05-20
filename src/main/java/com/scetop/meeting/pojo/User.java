package com.scetop.meeting.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String name;
    private String dept;
    private String post;
    private Long gender;
    @TableField(exist = false)
    private String base64;
    @TableField(exist = false)
    private String faceId;
}
