package com.eastwind.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eastwind.common.CustomException;
import com.eastwind.entity.Setmeal;
import com.eastwind.entity.SetmealDish;
import com.eastwind.entity.SetmealDto;
import com.eastwind.mapper.SetmealMapper;
import com.eastwind.service.SetmealDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SetMealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements com.eastwind.service.SetMealService {
    @Autowired
    SetmealDishService setmealDishService;

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        // 将setmealDto传递进来，保存套餐表的基本信息
        this.save(setmealDto);
        // 得到该套餐对应的菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item) -> {
            // 并让每一个菜品都设置一下套餐id,因为传递的时候没法给菜品设置套餐id
            // 因为传递的时候套餐id是需要在套餐数据保存后使用算法生成的，在保存数据后，才能让关联的菜品设置套餐id
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 用来删除套餐
     * */
    @Override
    public void removeWithDish(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        // 根据这一组id来查询对应的套餐(删除多个套餐的情况)
        queryWrapper.in(Setmeal::getId,ids);

        // 查询这组被查出来的数据，状态是否为1
        queryWrapper.eq(Setmeal::getStatus,1);

        // 得到查询的结果
        int count = this.count(queryWrapper);
        if (count > 0){
            // count　> 0 ：说明删除的数据中，包含在售的套餐
            throw new CustomException("套餐正在售卖中，请先停售再进行删除");
        }

        // 如果没有在售套餐，则直接删除
        this.removeByIds(ids);

        // 这里删除的是套餐所绑定的菜品
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        // 套餐菜品表中的套餐id，是否存在于这个传入的套餐id中
        wrapper.in(SetmealDish::getSetmealId,ids);
        // 存在的，都删除掉
        setmealDishService.remove(wrapper);
    }
}