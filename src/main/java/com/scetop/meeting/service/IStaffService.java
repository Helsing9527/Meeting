package com.scetop.meeting.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scetop.meeting.pojo.Staff;

public interface IStaffService extends IService<Staff> {

    IPage<Staff> getPage(Integer currentPage, Integer pageSize, Staff staff);
}
