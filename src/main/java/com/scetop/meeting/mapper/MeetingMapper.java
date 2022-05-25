package com.scetop.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scetop.meeting.pojo.Apply;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MeetingMapper extends BaseMapper<Apply> {
}
