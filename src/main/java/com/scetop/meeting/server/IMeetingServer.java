package com.scetop.meeting.server;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scetop.meeting.pojo.Apply;

import java.util.List;

public interface IMeetingServer extends IService<Apply> {
    Boolean createMeeting(Apply apply);

    Boolean updateMeeting(Apply apply);

    IPage<Apply> getPage(Integer currentPage, Integer pageSize);

    List<Integer> getParticipate(Integer id);
}
