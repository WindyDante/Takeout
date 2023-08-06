package com.eastwind.service;

/*
@author zhangJH
@create 2023-07-31-16:21
*/


import com.baomidou.mybatisplus.extension.service.IService;
import com.eastwind.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
