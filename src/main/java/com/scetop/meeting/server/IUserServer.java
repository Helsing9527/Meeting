package com.scetop.meeting.server;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scetop.meeting.pojo.User;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;

public interface IUserServer extends IService<User> {
    Boolean saveFaceId(String faceId, Integer id);
}
