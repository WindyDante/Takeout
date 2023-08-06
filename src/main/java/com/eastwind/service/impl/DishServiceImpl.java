package com.eastwind.service.impl;

/*
@author zhangJH
@create 2023-07-25-15:34
*/


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eastwind.entity.Dish;
import com.eastwind.entity.DishDto;
import com.eastwind.entity.DishFlavor;
import com.eastwind.mapper.DishMapper;
import com.eastwind.service.DisFlavorService;
import com.eastwind.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DisFlavorService dishFlavorService;



    // 该方法可以查询菜品对应的口味，用于回显到页面上
    public DishDto getByIdWithFlavor(Long id){
        // 通过this，指向Dish对象，利用id得到Dish对象
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        // 将旧菜品的数据，拷贝到新菜品中
        BeanUtils.copyProperties(dish,dishDto);

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        // 找出口味表中菜品的id与传入的当前菜品表中的id对应的数据
        queryWrapper.eq(DishFlavor::getId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        // 让新菜品设置口味，因为旧菜品不包含口味属性
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    // 保存菜品对应的口味
    public void saveWithFlavor(DishDto dishDto) {
        //将菜品数据保存到dish表
        this.save(dishDto);
        //获取dishId
        Long dishId = dishDto.getId();
        //将获取到的dishId赋值给dishFlavor的dishId属性
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor dishFlavor : flavors) {
            dishFlavor.setDishId(dishId);
        }
        //同时将菜品口味数据保存到dish_flavor表
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        // 将菜品数据更新到dish表中
        this.updateById(dishDto);

        // 清理之前的菜品口味表，然后增添新的菜品口味上去
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        // 比较菜品口味表中的id与菜品表的id是否相同，如果相同，就删除
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        // 删除菜品对应的口味
        dishFlavorService.remove(queryWrapper);

        // 添加当前提交过来的口味数据,对getFlavors数据进行数据插入
        List<DishFlavor> flavors = dishDto.getFlavors();

        // 对口味数据进行菜品表id数据的更新
        flavors = flavors.stream().map((item) -> {
            // 设置item(口味)，也就是菜品表的id为当前菜品
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //同时将菜品口味数据保存到dish_flavor表
        dishFlavorService.saveBatch(flavors);
    }
}

