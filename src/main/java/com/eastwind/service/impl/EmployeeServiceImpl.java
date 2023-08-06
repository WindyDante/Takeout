package com.eastwind.service.impl;

/*
@author zhangJH
@create 2023-07-22-21:15
*/


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eastwind.entity.Employee;
import com.eastwind.mapper.EmployeeMapper;
import com.eastwind.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
