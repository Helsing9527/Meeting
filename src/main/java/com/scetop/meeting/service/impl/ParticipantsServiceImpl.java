package com.scetop.meeting.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scetop.meeting.mapper.ParticipantsMapper;
import com.scetop.meeting.pojo.Participants;
import com.scetop.meeting.service.IParticipantsService;
import org.springframework.stereotype.Service;

@Service
public class ParticipantsServiceImpl extends ServiceImpl<ParticipantsMapper, Participants> implements IParticipantsService {
}
