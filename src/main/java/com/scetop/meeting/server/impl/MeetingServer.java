package com.scetop.meeting.server.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scetop.meeting.mapper.MeetingMapper;
import com.scetop.meeting.pojo.Apply;
import com.scetop.meeting.server.IMeetingServer;
import org.springframework.stereotype.Service;

@Service
public class MeetingServer extends ServiceImpl<MeetingMapper, Apply> implements IMeetingServer {
}
