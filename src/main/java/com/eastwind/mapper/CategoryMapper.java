package com.eastwind.mapper;

/*
@author zhangJH
@create 2023-07-25-11:08
*/


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eastwind.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
