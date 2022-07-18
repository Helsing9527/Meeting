package com.scetop.meeting.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scetop.meeting.pojo.Meeting;
import com.scetop.meeting.pojo.Participants;
import com.scetop.meeting.pojo.Staff;

import java.util.List;

public interface IMeetingService extends IService<Meeting> {
    IPage<Meeting> getPage(Integer currentPage, Integer pageSize, Meeting meeting);

    Boolean createMeeting(Meeting meeting);

    Boolean updateMeeting(Meeting meeting);

    Boolean deleteMeeting(Integer id);

    List<Staff> checkIn(Integer id);

    Participants checkInCount(Integer id);

    List<Meeting> queryMyCreatedMeeting(Integer id);

    List<Meeting> queryMyParticipantsMeeting(Integer id);

    Boolean recognizeFaceCheckIn(Staff staff);
}
