package com.eastwind.mapper;

/*
@author zhangJH
@create 2023-07-22-21:14
*/


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eastwind.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
