package com.eastwind.service.impl;

/*
@author zhangJH
@create 2023-07-31-14:46
*/


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eastwind.entity.ShoppingCart;
import com.eastwind.mapper.ShoppingCartMapper;
import com.eastwind.service.ShoppingService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingService {
}
