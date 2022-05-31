package com.scetop.meeting.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scetop.meeting.mapper.MeetingMapper;
import com.scetop.meeting.pojo.Apply;
import com.scetop.meeting.pojo.Participate;
import com.scetop.meeting.server.IMeetingServer;
import com.scetop.meeting.server.IParticipateServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MeetingServerImpl extends ServiceImpl<MeetingMapper, Apply> implements IMeetingServer {

    @Autowired
    private MeetingMapper meetingMapper;

    @Autowired
    private IParticipateServer participateServer;

    @Override
    public Boolean createMeeting(Apply apply) {
        boolean flag = save(apply);
        Boolean saveParticipate = saveParticipate(apply);
        return flag;
    }

    @Override
    public Boolean updateMeeting(Apply apply) {
        boolean flag = updateById(apply);
        // 根据会议id删除掉所有参会人员
        LambdaQueryWrapper<Participate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Participate::getApply_id, apply.getId());
        participateServer.remove(lambdaQueryWrapper);
        // 再存入新的参会人员
        saveParticipate(apply);
        return flag;
    }

    @Override
    public IPage<Apply> getPage(Integer currentPage, Integer pageSize) {
        IPage<Apply> page = new Page<>(currentPage, pageSize);
        meetingMapper.selectPage(page,null);
        return page;
    }

    @Override
    public List<Integer> getParticipate(Integer id) {
        List<Integer> personIds = new ArrayList<>();
        LambdaQueryWrapper<Participate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Participate::getApply_id, id);
        List<Participate> list = participateServer.list(lambdaQueryWrapper);
        for (Participate participate : list) {
            Integer userId = participate.getUser_id();
            personIds.add(userId);
        }
        return personIds;
    }

    // 新增参会人员
    private Boolean saveParticipate(Apply apply) {
        Boolean flag = true;
        Participate participate = new Participate();
        participate.setApply_id(apply.getId());
        // 获取所有参会人员id
        String[] persons = apply.getPersons();
        for (String person : persons) {
            participate.setId(null);
            participate.setUser_id(Integer.parseInt(person));
            boolean save = participateServer.save(participate);
        }
        return flag;
    }

}
