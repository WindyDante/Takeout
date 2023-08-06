package com.eastwind.mapper;

/*
@author zhangJH
@create 2023-07-28-14:36
*/


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eastwind.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
