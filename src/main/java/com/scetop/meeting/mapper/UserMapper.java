package com.scetop.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scetop.meeting.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
