package com.scetop.meeting.pojo;

import lombok.Data;

/**
 * 只是为了Restful接收前端传回的base64中符号不被UTF-8解析为%2F
 */
@Data
public class Base64 {
    private String loginBase64;
}
