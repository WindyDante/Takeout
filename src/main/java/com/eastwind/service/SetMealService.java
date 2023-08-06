package com.eastwind.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eastwind.entity.Setmeal;
import com.eastwind.entity.SetmealDto;

import java.util.List;

public interface SetMealService extends IService<Setmeal> {
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
}