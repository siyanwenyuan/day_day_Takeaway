package com.chen.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chen.reggie.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.lang.UsesSunHttpServer;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
