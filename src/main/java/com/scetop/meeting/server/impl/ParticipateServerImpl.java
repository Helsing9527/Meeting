package com.scetop.meeting.server.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scetop.meeting.mapper.ParticipateMapper;
import com.scetop.meeting.pojo.Participate;
import com.scetop.meeting.server.IParticipateServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParticipateServerImpl extends ServiceImpl<ParticipateMapper, Participate> implements IParticipateServer {
}
