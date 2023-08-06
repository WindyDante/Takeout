package com.eastwind.mapper;

/*
@author zhangJH
@create 2023-07-25-15:28
*/


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eastwind.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
