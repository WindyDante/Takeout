package com.eastwind.service.impl;

/*
@author zhangJH
@create 2023-07-27-8:35
*/


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eastwind.entity.DishFlavor;
import com.eastwind.mapper.DisFlavorMapper;
import com.eastwind.service.DisFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DisFlavorServiceImpl extends ServiceImpl<DisFlavorMapper, DishFlavor> implements DisFlavorService {
}
