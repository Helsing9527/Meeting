package com.scetop.meeting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scetop.meeting.controller.util.R;
import com.scetop.meeting.pojo.Apply;
import com.scetop.meeting.pojo.Group;
import com.scetop.meeting.pojo.User;
import com.scetop.meeting.server.IMeetingServer;
import com.scetop.meeting.server.IUserServer;
import com.scetop.meeting.tencentapi.group.CreateGroup;
import com.scetop.meeting.tencentapi.group.DeleteGroup;
import com.scetop.meeting.tencentapi.group.GetGroupList;
import com.scetop.meeting.tencentapi.person.DeletePerson;
import com.scetop.meeting.tencentapi.person.ModifyPersonBaseInfo;
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

    @Autowired
    private ModifyPersonBaseInfo modifyPersonBaseInfo;

    @Autowired
    private DeletePerson deletePerson;

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

    // 会议列表 根据id查询所有参会人员姓名
    @GetMapping("/ids/{ids}")
    public R queryIds(@PathVariable List ids) {
        List list = userServer.listByIds(ids);
        return new R(true, list, null);
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

    // 根据id查询人员信息  回填修改弹窗/会议签到权限
    @GetMapping("/person/{id}")
    public R queryPersonById(@PathVariable String id) {
        User user = userServer.getById(id);
        if (user != null) {
            return new R(true, user, "修改成功");
        }
        return new R(false, null, "数据同步失败，自动刷新");
    }

    // 修改人员信息
    @PutMapping("/person")
    public R updatePerson(@RequestBody User user) {
        boolean flag = userServer.updateById(user);
        if (flag) {
            modifyPersonBaseInfo.modifyPersonBaseInfo(user);
            return new R(true, null, "修改成功");
        } else {
            return new R(false, null, "数据同步失败，自动刷新");
        }
    }

    // 删除人员信息
    @DeleteMapping("/person/{id}")
    public R removePerson(@PathVariable String id) {
        boolean flag = userServer.removeById(id);
        if (flag) {
            deletePerson.deletePerson(id);
            return new R(true, null, "删除成功");
        } else {
            return new R(false, null, "数据同步失败，自动刷新");
        }
    }
}
