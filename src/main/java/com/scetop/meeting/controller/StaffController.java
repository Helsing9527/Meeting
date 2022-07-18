package com.scetop.meeting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.scetop.meeting.common.R;
import com.scetop.meeting.compreFace.AddExample;
import com.scetop.meeting.compreFace.DeleteExample;
import com.scetop.meeting.pojo.Staff;
import com.scetop.meeting.service.IStaffService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private IStaffService staffService;

    @Autowired
    private AddExample addExample;

    @Autowired
    private DeleteExample deleteExample;

    // 分页查询人员列表
    @GetMapping("/list/{currentPage}/{pageSize}")
    public R queryStaff(@PathVariable Integer currentPage, @PathVariable Integer pageSize, Staff staff) {
        IPage<Staff> page = staffService.getPage(currentPage, pageSize, staff);
        if (currentPage > page.getPages()) {
            page = staffService.getPage((int) page.getPages(), pageSize, staff);
        }
        return new R(true, page, null);
    }

    // 员工入职
    @PostMapping("/hired")
    public R staffHired(@RequestBody Staff staff) {
        boolean save = staffService.save(staff);
        if (save) {
            Boolean example = addExample.addExample(staff);
            if (example) {
                return new R(true, null, "员工入职成功");
            }
            // 人脸录入失败删除存入的数据库
            return new R(false, null, "人脸录入失败，请重新办理入职");
        }
        return new R(false, null, "个人信息录入失败，请重新办理入职");
    }

    // 修改员工信息
    @PutMapping("/edit")
    public R staffPut(@RequestBody Staff staff) {
        boolean update = staffService.updateById(staff);
        if (update) {
            return new R(true, null, "修改成功");
        }
        return new R(false, null, "修改失败，数据同步失败，即将刷新");
    }

    // 员工离职
    @DeleteMapping("/dimission/{id}")
    public R staffDimission(@PathVariable Integer id) {
        Staff staff = staffService.getById(id);
        Boolean example = deleteExample.deleteExample(staff);
        if (example) {
            boolean remove = staffService.removeById(id);
            if (remove) {
                return new R(true, null, "员工离职成功");
            }
            return new R(false, null, "员工信息删除失败，请至数据库内删除");
        }
        return new R(false, null, "人脸数据删除失败，请管理员至管理页面删除");
    }

}
