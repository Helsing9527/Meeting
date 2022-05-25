package com.scetop.meeting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scetop.meeting.controller.util.R;
import com.scetop.meeting.pojo.Apply;
import com.scetop.meeting.pojo.User;
import com.scetop.meeting.server.IMeetingServer;
import com.scetop.meeting.server.IUserServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingController {
    @Autowired
    private IUserServer userServer;

    @Autowired
    private IMeetingServer meetingServer;

    // 会议申请 人员列表 分页
    @GetMapping("/{currentPage}/{pageSize}")
    public R queryAll(@PathVariable Integer currentPage, @PathVariable Integer pageSize) {
        IPage<User> page = userServer.getPage(currentPage, pageSize);
        return new R(true, page, null);
    }

    // 会议申请 表单
    @PostMapping
    public R createMeeting(@RequestBody Apply apply) {
        boolean flag = meetingServer.save(apply);
        if (flag) {
            return new R(true, null, "会议创建成功");
        } else {
            return new R(false, null, "会议创建失败");
        }
    }

    // 会议列表 表格
    @GetMapping
    public R meetingTable() {
        List<Apply> list = meetingServer.list();
        return new R(true, list, null);
    }

    // 会议列表 删除
    @DeleteMapping("/{id}")
    public R meetingDelete(@PathVariable String id) {
        boolean flag = meetingServer.removeById(id);
        if (flag) {
            return new R(true, null, "删除成功");
        } else {
            return new R(false, null, "数据同步失败，自动刷新");
        }
    }

    // 会议列表 编辑 根据id查询用户信息 回填表单
    @GetMapping("/{id}")
    public R selectById(@PathVariable String id) {
        Apply apply = meetingServer.getById(id);
        return new R(true, apply, null);
    }

    // 会议列表 编辑 更新
    @PutMapping
    public R update(@RequestBody Apply apply) {
        boolean flag = meetingServer.updateById(apply);
        if (flag) {
            return new R(true, null, "修改成功");
        } else {
            return new R(false, null, "数据同步失败，自动刷新");
        }
    }
}
