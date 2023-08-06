package com.eastwind.controller;

/*
@author zhangJH
@create 2023-07-31-16:25
*/


import com.eastwind.common.Result;
import com.eastwind.entity.Orders;
import com.eastwind.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public Result<String> submit(@RequestBody Orders orders){
        log.info(orders.toString());
        orderService.submit(orders);
        return Result.success("用户下单成功");
    }
}
