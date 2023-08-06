package com.eastwind.mapper;

/*
@author zhangJH
@create 2023-07-31-14:45
*/


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eastwind.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
