package com.eastwind.service;

/*
@author zhangJH
@create 2023-07-25-15:32
*/


import com.baomidou.mybatisplus.extension.service.IService;
import com.eastwind.entity.Dish;
import com.eastwind.entity.DishDto;

public interface DishService extends IService<Dish> {
    void saveWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void updateWithFlavor(DishDto dishDto);
}
