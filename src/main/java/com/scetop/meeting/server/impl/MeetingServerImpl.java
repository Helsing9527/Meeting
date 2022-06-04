package com.scetop.meeting.server.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scetop.meeting.mapper.MeetingMapper;
import com.scetop.meeting.pojo.Apply;
import com.scetop.meeting.pojo.Participate;
import com.scetop.meeting.server.IMeetingServer;
import com.scetop.meeting.server.IParticipateServer;
import com.scetop.meeting.tencentapi.face.VerifyFace;
import com.scetop.meeting.tencentapi.person.GetPersonList;
import org.apache.logging.log4j.util.Strings;
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

    @Autowired
    private GetPersonList getPersonList;

    @Autowired
    private VerifyFace verifyFace;

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
        if (apply.getPersons() != null) {
            saveParticipate(apply);
        }
        return flag;
    }

    @Override
    public IPage<Apply> getPage(Integer currentPage, Integer pageSize, Apply apply) {
        LambdaQueryWrapper<Apply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Strings.isNotEmpty(apply.getMeetingName()), Apply::getMeetingName, apply.getMeetingName());
        lambdaQueryWrapper.like(Strings.isNotEmpty(apply.getMeetingPlace()), Apply::getMeetingPlace, apply.getMeetingPlace());
        lambdaQueryWrapper.like(Strings.isNotEmpty(apply.getStatus()), Apply::getStatus, apply.getStatus());
        IPage<Apply> page = new Page<>(currentPage, pageSize);
        meetingMapper.selectPage(page, lambdaQueryWrapper);
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

    @Override
    public Boolean signIn(String imgBase64) {
        // 调用腾讯接口 返回签到人员faceId
        int userId = 0;
        List<String> personList = getPersonList.getPersonList();
        for (String personId : personList) {
            Boolean flag = verifyFace.verifyFace(imgBase64, personId);
            if (flag) {
                userId = Integer.parseInt(personId);
                break;
            }
        }
        if (userId != 0) {
            // 查询返回所有 进行中 会议id 遍历
            LambdaQueryWrapper<Apply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Apply::getStatus, "进行中");
            List<Apply> applyList = list(lambdaQueryWrapper);
            for (Apply apply : applyList) {
                // 更新签到状态
                LambdaUpdateWrapper<Participate> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(Participate::getUser_id, userId)
                        .eq(Participate::getApply_id, apply.getId())
                        .set(Participate::getStatus, "已签到");
                boolean flag = participateServer.update(lambdaUpdateWrapper);
                if (flag) {
                    return true;
                }
            }
        }
        return false;
    }

    // 提取公共代码 ===》》》新增/修改 参会人员
    private Boolean saveParticipate(Apply apply) {
        Boolean flag = true;
        Participate participate = new Participate();
        participate.setApply_id(apply.getId());
        // 获取所有参会人员id
        String[] persons = apply.getPersons();
        for (String person : persons) {
            participate.setId(null);
            participate.setUser_id(Integer.parseInt(person));
            flag = participateServer.save(participate);
            if (!flag) {
                return false;
            }
        }
        return flag;
    }

}
