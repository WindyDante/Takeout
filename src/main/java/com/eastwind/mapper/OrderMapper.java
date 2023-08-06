package com.eastwind.mapper;

/*
@author zhangJH
@create 2023-07-31-16:20
*/


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eastwind.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
}
