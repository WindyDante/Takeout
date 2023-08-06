package com.eastwind.controller;

/*
@author zhangJH
@create 2023-07-28-7:51
*/


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eastwind.common.CustomException;
import com.eastwind.common.Result;
import com.eastwind.entity.*;
import com.eastwind.service.CategoryService;
import com.eastwind.service.SetMealService;
import com.eastwind.service.SetmealDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<SetmealDto>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 得到该套餐项对应的菜品
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());

        // 添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Setmeal::getStatus,1);

        //添加排序条件(按照更新时间来排序)
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setMealService.list(queryWrapper);

        List<SetmealDto> setmealDtoList = list.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            // 分类id
            Long categoryId = item.getCategoryId();
            // 根据Id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null){
                // 如果分类对象查询到了，说明该套餐有分类
                String categoryName = category.getName();
                // 就让套餐设置一下这个分类对象名字
                setmealDto.setCategoryName(categoryName);
            }

            // 得到套餐的id
            Long itemId = item.getId();

            LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
            // 查找与当前套餐id相同的口味信息
            wrapper.eq(SetmealDish::getDishId,itemId);
            List<SetmealDish> flavors = setmealDishService.list(wrapper);
            // 设置菜品口味
            setmealDto.setSetmealDishes(flavors);
            return setmealDto;
        }).collect(Collectors.toList());
        return Result.success(setmealDtoList);
    }

    @DeleteMapping
    public Result<String> delete(@RequestParam List<Long> ids){
        if (ids.size() == 0){
            throw new CustomException("请选中你要删除的套餐");
        }
        setMealService.removeWithDish(ids);
        return Result.success("删除成功");
    }

    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        // 执行分页查询
        setMealService.page(pageInfo,queryWrapper);

        BeanUtils.copyProperties(pageInfo,dtoPage,"records");

        // 得到之前的数据，对它的分类进行赋值更新
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            // 将旧数据赋值给新数据，再对数据分类进行更新
            BeanUtils.copyProperties(item, setmealDto);
            // 得到套餐的Id,因为套餐关联了分类，所以我们可以去分类里查询相关的名称
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                // 将套餐分类名称赋值给新数据
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        dtoPage.setRecords(list);

        return Result.success(dtoPage);
    }

    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("套餐信息：{}", setmealDto);
        setMealService.saveWithDish(setmealDto);
        return Result.success("套餐添加成功");
    }

}
