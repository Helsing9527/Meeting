package com.scetop.meeting.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scetop.meeting.util.R;
import com.scetop.meeting.pojo.*;
import com.scetop.meeting.server.IMeetingServer;
import com.scetop.meeting.server.IParticipateServer;
import com.scetop.meeting.server.IUserServer;
import com.scetop.meeting.tencentapi.person.DeletePerson;
import com.scetop.meeting.tencentapi.person.ModifyPersonBaseInfo;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @Autowired
    private IParticipateServer participateServer;

    // 查询个人发起的会议
    @GetMapping("/myInitMeeting/{id}")
    public R queryMyInitMeeting(@PathVariable String id) {
        LambdaQueryWrapper<Apply> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Apply::getInitiator, id);
        List<Apply> applyList = meetingServer.list(lambdaQueryWrapper);
        return new R(true, applyList, null);
    }

    // 查询个人拥有的会议
    @GetMapping("/myMeeting/{id}")
    public R queryMyMeeting(@PathVariable String id) {
        List<Integer> apply_ids = new ArrayList<>();
        LambdaQueryWrapper<Participate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Strings.isNotEmpty(id), Participate::getUser_id, id);
        List<Participate> participateList = participateServer.list(lambdaQueryWrapper);
        for (Participate participate : participateList) {
            apply_ids.add(participate.getApply_id());
        }
        List<Apply> applyList = meetingServer.listByIds(apply_ids);
        return new R(true, applyList, null);
    }

    // 会议申请 人员列表 分页
    @GetMapping("/{currentPage}/{pageSize}")
    public R queryAll(@PathVariable Integer currentPage, @PathVariable Integer pageSize, User user) {
        IPage<User> page = userServer.getPage(currentPage, pageSize, user);
        if (currentPage > page.getPages()) {
            page = userServer.getPage((int) page.getPages(), pageSize, user);
        }
        return new R(true, page, null);
    }

    // 创建会议
    @PostMapping
    public R createMeeting(@RequestBody Apply apply) {
        Boolean flag = meetingServer.createMeeting(apply);
        if (flag) {
            return new R(true, null, "会议创建成功");
        }
        return new R(false, null, "会议创建失败");
    }

    // 会议列表 表格
    @GetMapping("/table/{currentPage}/{pageSize}")
    public R meetingTable(@PathVariable Integer currentPage, @PathVariable Integer pageSize, Apply apply) {
        IPage<Apply> page = meetingServer.getPage(currentPage, pageSize, apply);
        if (currentPage > page.getPages()) {
            page = meetingServer.getPage((int) page.getPages(), pageSize, apply);
        }
        return new R(true, page, null);
    }

    // 会议列表 删除
    @DeleteMapping("/{id}")
    public R meetingDelete(@PathVariable String id) {
        boolean flag = meetingServer.removeById(id);
        if (flag) {
            return new R(true, null, "删除成功");
        }
        return new R(false, null, "数据同步失败，自动刷新");
    }

    // 会议列表 参会人员/签到状态
    @GetMapping("/{id}")
    public R queryIds(@PathVariable Integer id) {
        // 获取所有参会人员 id
        List<Integer> participate = meetingServer.getParticipate(id);
        if (participate.isEmpty()) {
            return new R(false, null, null);
        }
        // 通过参会人员id 查询个人信息
        List<User> list = userServer.listByIds(participate);
        // 遍历添加签到状态
        for (User user : list) {
            LambdaQueryWrapper<Participate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Participate::getUser_id, user.getId()).eq(Participate::getApply_id, id);
            Participate one = participateServer.getOne(lambdaQueryWrapper);
            user.setStatus(one.getStatus());
        }
        return new R(true, list, null);
    }

    @GetMapping("/count/{id}")
    public R queryStatusCount(@PathVariable Integer id) {
        LambdaQueryWrapper<Participate> isSignIn = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Participate> noSignIn = new LambdaQueryWrapper<>();
        isSignIn.eq(Participate::getApply_id, id).eq(Participate::getStatus, "已签到");
        noSignIn.eq(Participate::getApply_id, id).eq(Participate::getStatus, "未签到");
        long isSignInCount = participateServer.count(isSignIn);
        long noSignInCount = participateServer.count(noSignIn);
        SignInCount signInCount = new SignInCount();
        signInCount.setIsSignIn(isSignInCount);
        signInCount.setNoSignIn(noSignInCount);
        return new R(true, signInCount, null);
    }

    // 会议列表 编辑 更新
    @PutMapping
    public R update(@RequestBody Apply apply) {
        Boolean flag = meetingServer.updateMeeting(apply);
        if (flag) {
            return new R(true, null, "修改成功");
        }
        return new R(false, null, "数据同步失败，自动刷新");
    }

    // 开始会议
    @PutMapping("/startMeeting/{id}")
    public R updateStatus(@PathVariable Integer id) {
        Apply apply = meetingServer.getById(id);
        if ("未开始".equals(apply.getStatus())) {
            LambdaUpdateWrapper<Apply> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.set(Apply::getStatus, "进行中");
            lambdaUpdateWrapper.eq(Apply::getId, id);
            boolean flag = meetingServer.update(lambdaUpdateWrapper);
            if (flag) {
                return new R(true, null, "开始会议，开放签到");
            } else {
                return new R(false, null, "数据同步失败，自动刷新");
            }
        } else if ("进行中".equals(apply.getStatus())) {
            return new R(true, null, "会议进行中");
        }
        return new R(false, null, "会议已结束");
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
        }
        return new R(false, null, "数据同步失败，自动刷新");
    }

    // 删除人员信息
    @DeleteMapping("/person/{id}")
    public R removePerson(@PathVariable String id) {
        boolean flag = userServer.removeById(id);
        if (flag) {
            deletePerson.deletePerson(id);
            return new R(true, null, "删除成功");
        }
        return new R(false, null, "数据同步失败，自动刷新");
    }

    // 签到管理
    @PostMapping("/signIn")
    public R signIn(@RequestBody Base64 imgBase64) {
        System.out.println(imgBase64);
        Boolean flag = meetingServer.signIn(imgBase64.getImgBase64());
        if (flag) {
            return new R(true, null, "签到成功！");
        }
        return new R(false, null, "签到失败");
    }
}
