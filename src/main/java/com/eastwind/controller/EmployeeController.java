package com.eastwind.controller;

/*
@author zhangJH
@create 2023-07-22-21:17
*/


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eastwind.common.Result;
import com.eastwind.entity.Employee;
import com.eastwind.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @GetMapping("/{id}")
    public Result<Employee> update(@PathVariable Long id){
        log.info("根据id查询数据");
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        }
        // 当查询结果为空，就说明没有该用户，或者有问题
        return Result.error("未查询到该员工信息");
    }

    @PutMapping
    public Result<String> update(@RequestBody Employee employee){
        log.info(employee.toString());
        // 关于设置公共信息创建者，更新者，创建时间，更新时间，在元数据对象处理器已经统一处理了
        employeeService.updateById(employee);
        return Result.success("员工信息修改成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
       // page:当前页码(startIndex)
        // pageSize:查询数量
        log.info("page={},pageSize={},name={}",page,pageSize,name);
        // 得到分页的构造器
        Page pageInfo = new Page(page,pageSize);

        // 添加过滤条件
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        // 如果name不为空，数据库就用like匹配外面的name字段,否则不匹配
        lqw.like(!(name == null || "".equals(name)),Employee::getName,name);

        // 对查询结果进行排序(更新时间)
        lqw.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo,lqw);

        return Result.success(pageInfo);
    }

    @PostMapping
    public Result<String> save(@RequestBody Employee employee){
        // 设置新密码默认为123456，并进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        // 关于设置公共信息创建者，更新者，创建时间，更新时间，在元数据对象处理器已经统一处理了

        // 保存用户信息
        employeeService.save(employee);

        return Result.success("添加员工成功");
    }

    @PostMapping("/login")
    // @RequestBody可以接收json字符串
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 1、对密码进行MD5加密
        String password = employee.getPassword();   // 获得密码
//        DigestUtils.md5DigestAsHex(输入流/字节数组)  所以需要将字符串转为字节
        password = DigestUtils.md5DigestAsHex(password.getBytes());     // 利用DigestUtils工具类进行加密

        // 2、查找用户是否存在(利用MP编写)
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();
        // Employee::getName数据库中的name数据,employee.getName()传入的name数据
        lqw.eq(Employee::getUsername,employee.getUsername());
        // 因为name设定了unique(唯一约束)，所以返回的对象唯一
        Employee emp = employeeService.getOne(lqw);

        // 3、不存在则返回登录失败msg
        if (emp == null){
            return Result.error("登录失败");
        }

        // 4、验证密码是否正确(加密后的密码与数据库内的密码比对[数据库的密码是加密过的])
        if (!(password.equals(emp.getPassword()))){
            // 如果密码相同，说明是正确的，继续往下判断，如果不同，说明密码是错误的，在这里结束
            return Result.error("登录失败");
        }

        // 5、验证员工状态，是否可用
        if (emp.getStatus() == 0){
            // 0不可用，1可用，当不可用时，则结束，否则继续往下判断
            return Result.error("账号已禁用");
        }


        // 6、将ID数据存到session域中传递过去
        request.getSession().setAttribute("employee",emp.getId());
        return Result.success(emp);
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return Result.success("登出成功");
    }
}
