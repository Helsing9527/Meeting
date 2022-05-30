package com.scetop.meeting.server;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scetop.meeting.pojo.Apply;

public interface IMeetingServer extends IService<Apply> {
    Boolean createMeeting(Apply apply);

    Boolean updateMeeting(Apply apply);
}
