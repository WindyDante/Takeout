package com.eastwind.service.impl;

/*
@author zhangJH
@create 2023-07-31-16:23
*/


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eastwind.entity.OrderDetail;
import com.eastwind.mapper.OrderDetailMapper;
import com.eastwind.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
