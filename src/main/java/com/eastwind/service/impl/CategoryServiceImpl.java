package com.eastwind.service.impl;

/*
@author zhangJH
@create 2023-07-25-11:09
*/


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eastwind.common.CustomException;
import com.eastwind.entity.Category;
import com.eastwind.entity.Dish;
import com.eastwind.entity.Setmeal;
import com.eastwind.mapper.CategoryMapper;
import com.eastwind.service.CategoryService;
import com.eastwind.service.DishService;
import com.eastwind.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    DishService dishService;

    @Autowired
    SetMealService setMealService;

    @Override
    public void delete(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int disCount = dishService.count(dishLambdaQueryWrapper);
        if (disCount > 0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = setMealService.count(setmealLambdaQueryWrapper);
        if (setmealCount > 0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        // 如果没有找到关联的，就正常删除
        super.removeById(id);
    }
}
