package com.scetop.meeting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scetop.meeting.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Update("update user set faceId = #{faceId} where id = #{id}")
    Boolean saveFaceId(String faceId, Integer id);
}
