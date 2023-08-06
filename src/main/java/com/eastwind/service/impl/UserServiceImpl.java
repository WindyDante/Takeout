package com.eastwind.service.impl;

/*
@author zhangJH
@create 2023-07-28-14:37
*/


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eastwind.entity.User;
import com.eastwind.mapper.UserMapper;
import com.eastwind.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
