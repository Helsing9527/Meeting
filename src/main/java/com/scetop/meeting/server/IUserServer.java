package com.scetop.meeting.server;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scetop.meeting.pojo.User;

public interface IUserServer extends IService<User> {
    Boolean saveFaceId(String faceId, Integer id);
}
