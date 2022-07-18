package com.scetop.meeting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scetop.meeting.mapper.StaffMapper;
import com.scetop.meeting.pojo.Staff;
import com.scetop.meeting.service.IStaffService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements IStaffService {

    @Autowired
    private StaffMapper staffMapper;

    @Override
    public IPage<Staff> getPage(Integer currentPage, Integer pageSize, Staff staff) {
        LambdaQueryWrapper<Staff> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(Strings.isNotEmpty(staff.getUserName()), Staff::getUserName, staff.getUserName());
        lambdaQueryWrapper.like(Strings.isNotEmpty(staff.getDept()), Staff::getDept, staff.getDept());
        lambdaQueryWrapper.like(Strings.isNotEmpty(staff.getPost()), Staff::getPost, staff.getPost());
        IPage<Staff> page = new Page<>(currentPage, pageSize);
        staffMapper.selectPage(page, lambdaQueryWrapper);
        return page;
    }
}
