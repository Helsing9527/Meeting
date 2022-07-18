package com.scetop.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scetop.meeting.compreFace.RecognizeFace;
import com.scetop.meeting.mapper.MeetingMapper;
import com.scetop.meeting.pojo.Meeting;
import com.scetop.meeting.pojo.Participants;
import com.scetop.meeting.pojo.Staff;
import com.scetop.meeting.service.IMeetingService;
import com.scetop.meeting.service.IParticipantsService;
import com.scetop.meeting.service.IStaffService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class MeetingSerciveImpl extends ServiceImpl<MeetingMapper, Meeting> implements IMeetingService {

    @Autowired
    private MeetingMapper meetingMapper;

    @Autowired
    private IParticipantsService participantsService;

    @Autowired
    private IStaffService staffService;

    @Autowired
    private RecognizeFace recognizeFace;

    // 分页查询 会议列表
    @Override
    public IPage<Meeting> getPage(Integer currentPage, Integer pageSize, Meeting meeting) {
        LambdaQueryWrapper<Meeting> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Strings.isNotEmpty(meeting.getMeetingName()), Meeting::getMeetingName, meeting.getMeetingName());
        lambdaQueryWrapper.like(Strings.isNotEmpty(meeting.getMeetingRoom()), Meeting::getMeetingRoom, meeting.getMeetingRoom());
        lambdaQueryWrapper.like(Strings.isNotEmpty(meeting.getSponsor()), Meeting::getSponsor, meeting.getSponsor());
        IPage<Meeting> page = new Page<>(currentPage, pageSize);
        meetingMapper.selectPage(page, lambdaQueryWrapper);
        return page;
    }

    // 创建会议
    @Override
    public Boolean createMeeting(Meeting meeting) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {
            date = simpleDateFormat.parse(meeting.getEndTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        meeting.setTimestamp(date.getTime() - 28800000);
        boolean save = save(meeting);
        if (save) {
            Boolean saveParticipants = saveParticipants(meeting);
            if (saveParticipants) {
                return true;
            }
        }
        removeById(meeting);
        return false;
    }

    // 编辑会议
    @Override
    public Boolean updateMeeting(Meeting meeting) {
        if (meeting.getParticipants() != null) {
            boolean update = updateById(meeting);
            if (update) {
                LambdaQueryWrapper<Participants> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(Participants::getMeetingId, meeting.getId());
                boolean remove = participantsService.remove(lambdaQueryWrapper);
                if (remove) {
                    Boolean saveParticipants = saveParticipants(meeting);
                    if (saveParticipants) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    // 删除会议
    @Override
    public Boolean deleteMeeting(Integer id) {
        LambdaQueryWrapper<Participants> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Participants::getMeetingId, id);
        boolean removeParticipants = participantsService.remove(lambdaQueryWrapper);
        if (removeParticipants) {
            boolean removeById = removeById(id);
            if (removeById) {
                return true;
            }
        }
        return false;
    }

    // 参会人员/签到详情
    @Override
    public List<Staff> checkIn(Integer id) {
        // 通过 会议id 查询 参会表 中的 参会人员id 和 签到状态
        LambdaQueryWrapper<Participants> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Participants::getMeetingId, id);
        // 所有此次会议参与者
        List<Participants> list = participantsService.list(lambdaQueryWrapper);
        // 初始化返回结果
        List<Staff> meeting_staff = new ArrayList<>();
        // 遍历个人信息并注入签到状态
        for (Participants participants : list) {
            Staff staff = staffService.getById(participants.getStaffId());
            staff.setStatus(participants.getCheckInStatus());
            meeting_staff.add(staff);
        }
        return meeting_staff;
    }

    // 参会人员签到统计
    @Override
    public Participants checkInCount(Integer id) {
        LambdaQueryWrapper<Participants> checkInWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Participants> notCheckInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.eq(Participants::getMeetingId, id).eq(Participants::getCheckInStatus, "已签到");
        notCheckInWrapper.eq(Participants::getMeetingId, id).eq(Participants::getCheckInStatus, "未签到");
        int countCheckIn = (int) participantsService.count(checkInWrapper);
        int countNotCheckIn = (int) participantsService.count(notCheckInWrapper);
        Participants participants = new Participants();
        participants.setTotal(countCheckIn + countNotCheckIn);
        participants.setCheckIn(countCheckIn);
        participants.setNotCheckIn(countNotCheckIn);
        return participants;
    }

    // 我发起的会议
    @Override
    public List<Meeting> queryMyCreatedMeeting(Integer id) {
        Staff staff = staffService.getById(id);
        LambdaQueryWrapper<Meeting> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Meeting::getSponsor, staff.getUserName());
        List<Meeting> list = list(lambdaQueryWrapper);
        return list;
    }

    // 我参与的会议
    @Override
    public List<Meeting> queryMyParticipantsMeeting(Integer id) {
        LambdaQueryWrapper<Participants> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Participants::getStaffId, id);
        List<Participants> participantsList = participantsService.list(lambdaQueryWrapper);
        List<Meeting> meetingList = new ArrayList<>();
        for (Participants participants : participantsList) {
            Meeting meeting = getById(participants.getMeetingId());
            meeting.setCheckInStatus(participants.getCheckInStatus());
            meetingList.add(meeting);
        }
        return meetingList;
    }

    // 会议签到
    @Override
    public Boolean recognizeFaceCheckIn(Staff staff) {
        // 返回人脸库中的人员名称（staffId_staffUserName)
        String staffUserName = recognizeFace.recognizeFace(staff.getImgBase64());
        // 正则表达式获取 staffId
        String regEx = "[^0-9]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(staffUserName);
        String staffId = matcher.replaceAll("").trim();
        // 返回为空则签到失败
        if (staff != null) {
            // 根据员工 id 查询该员工所有会议id
            LambdaQueryWrapper<Participants> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Participants::getStaffId, staffId);
            List<Participants> participantsList = participantsService.list(lambdaQueryWrapper);
            List<Integer> meetingIds = new ArrayList<>();
            for (Participants participants : participantsList) {
                meetingIds.add(participants.getMeetingId());
            }
            // 遍历出 进行中 的会议
            Integer meetingId = null;
            List<Meeting> meetingList = listByIds(meetingIds);
            for (Meeting meeting : meetingList) {
                if ("进行中".equals(meeting.getStatus())) {
                    meetingId = meeting.getId();
                    break;
                }
            }
            if (meetingId != null) {
                // 设置签到状态
                LambdaUpdateWrapper<Participants> lambdaUpdateWrapper = new LambdaUpdateWrapper();
                lambdaUpdateWrapper.eq(Participants::getMeetingId, meetingId).eq(Participants::getStaffId, staffId).set(Participants::getCheckInStatus, "已签到");
                participantsService.update(lambdaUpdateWrapper);
                return true;
            }
            return false;
        }
        return false;
    }

    // 新增/修改 参会人员
    private Boolean saveParticipants(Meeting meeting) {
        Boolean flag = true;
        Participants participants = new Participants();
        // 设置本次会议id
        participants.setMeetingId(meeting.getId());
        // 得到所有参会人员id
        String[] staffIds = meeting.getParticipants();
        // 遍历逐个存入
        for (String staffId : staffIds) {
            // 防止存入参与表 id 复用导致存入失败
            participants.setId(null);
            participants.setCheckInStatus("未签到");
            participants.setStaffId(Integer.parseInt(staffId));
            flag = participantsService.save(participants);
            if (!flag) {
                flag = false;
            }
        }
        return flag;
    }
}
