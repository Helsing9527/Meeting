package com.scetop.meeting.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scetop.meeting.pojo.Meeting;
import com.scetop.meeting.pojo.Participants;
import com.scetop.meeting.pojo.Staff;
import com.scetop.meeting.service.IMeetingService;
import com.scetop.meeting.common.R;
import com.scetop.meeting.service.IParticipantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Timer;

@RestController
@RequestMapping("/meeting")
public class MeetingController {

    @Autowired
    private IMeetingService meetingService;

    @Autowired
    private IParticipantsService participantsService;

    /**
     * 我的会议
     */
    // 我的会议 我发起的会议
    @GetMapping("/myCreatedMeeting/{id}")
    public R queryMyCreatedMeeting(@PathVariable Integer id) {
        List<Meeting> list = meetingService.queryMyCreatedMeeting(id);
        return new R(true, list, null);
    }

    // 我的会议 我参与的会议
    @GetMapping("/myParticipantsMeeting/{id}")
    public R queryMyParticipantsMeeting(@PathVariable Integer id) {
        List<Meeting> list = meetingService.queryMyParticipantsMeeting(id);
        return new R(true, list, null);
    }

    /**
     * 会议管理
     */

    // 分页查询 会议列表
    @GetMapping("list/{currentPage}/{pageSize}")
    public R queryMeeting(@PathVariable Integer currentPage, @PathVariable Integer pageSize, Meeting meeting) {
        IPage<Meeting> page = meetingService.getPage(currentPage, pageSize, meeting);
        if (currentPage > page.getPages()) {
            page = meetingService.getPage((int) page.getPages(), pageSize, meeting);
        }
        return new R(true, page, null);
    }

    // 创建会议
    @PostMapping("/create")
    public R createMeeting(@RequestBody Meeting meeting) {
        Boolean create = meetingService.createMeeting(meeting);
        if (create) {
            return new R(true, null, "会议创建成功");
        }
        return new R(false, null, "会议创建失败");
    }

    // 编辑会议
    @PutMapping("/edit")
    public R editMeeting(@RequestBody Meeting meeting) {
        Boolean edit = meetingService.updateMeeting(meeting);
        if (edit) {
            return new R(true, null, "会议修改成功");
        }
        return new R(false, null, "数据同步失败，即将刷新列表");
    }

    // 开始会议
    @PutMapping("/start")
    public R startMeeting(@RequestBody Meeting meeting) {
        if ("未开始".equals(meeting.getStatus())) {
            LambdaUpdateWrapper<Meeting> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(Meeting::getId, meeting.getId()).set(Meeting::getStatus, "进行中");
            boolean start = meetingService.update(lambdaUpdateWrapper);
            if (start) {
                return new R(true, null, "会议开始，请开始组织签到");
            }
            return new R(false, null, "数据同步失败，即将刷新列表");
        } else if ("进行中".equals(meeting.getStatus())) {
            return new R(false, null, "会议已经开始咯~ 请开始组织签到");
        } else {
            return new R(false, null, "搞什么！会议都结束了.......");
        }
    }

    // 会议代签
    @PutMapping("/helpCheckIn/{staffId}/{meetingId}")
    public R helpCheckIn(@PathVariable Integer staffId, @PathVariable Integer meetingId) {
        LambdaUpdateWrapper<Participants> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Participants::getStaffId, staffId).eq(Participants::getMeetingId, meetingId).set(Participants::getCheckInStatus, "已签到");
        boolean update = participantsService.update(lambdaUpdateWrapper);
        if (update) {
            return new R(true, null, "代签成功");
        }
        return new R(false, null, "数据同步失败，自动刷新...");
    }

    // 删除会议
    @DeleteMapping("/delete/{id}")
    public R deleteMeeting(@PathVariable Integer id) {
        Boolean delete = meetingService.deleteMeeting(id);
        if (delete) {
            return new R(true, null, "会议删除成功");
        }
        return new R(false, null, "数据同步失败，即将刷新列表");
    }

    // 参会人员/签到详情
    @GetMapping("/checkIn/{id}")
    public R checkIn(@PathVariable Integer id) {
        List<Staff> list = meetingService.checkIn(id);
        return new R(true, list, null);
    }

    // 参会人员签到统计
    @GetMapping("/checkInCount/{id}")
    public R checkInCount(@PathVariable Integer id) {
        Participants participants = meetingService.checkInCount(id);
        return new R(true, participants, null);
    }

    // 自动结束会议
    @Scheduled(cron = "0 0/5 * * * ?")
    private void autoEndMeeting() {
        try {
            long currentTimeMillis = new Date().getTime();
            List<Meeting> meetingList = meetingService.list();
            LambdaUpdateWrapper<Meeting> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            for (Meeting meeting : meetingList) {
                lambdaUpdateWrapper.lt(Meeting::getTimestamp, currentTimeMillis).set(Meeting::getStatus, "已结束");
                meetingService.update(lambdaUpdateWrapper);
            }
        } catch (Exception e) {
            return;
        }
    }

    /**
     * 签到页面
     * @param staff
     * @return
     */

    // 签到
    @PutMapping("/checkIn")
    public R checkIn(@RequestBody Staff staff) {
        if (staff.getImgBase64().length() / 1024 >= 10) {
            Boolean flag = meetingService.recognizeFaceCheckIn(staff);
            if (flag) {
                return new R(true, null, "签到成功");
            }
            return new R(false, null, "签到失败，光线过暗或过亮！请重新签到或联系发起人进行代签~");
        }
        return new R(false, null, "照片异常，请重新签到");
    }

    // 安全退出
    @GetMapping("/exit")
    public R exit(HttpServletRequest request) {
        request.getSession().removeAttribute("userInfo");
        return new R(true, null, "已安全退出，即将跳转登录页");
    }
}
