package com.eastwind.controller;

/*
@author zhangJH
@create 2023-07-31-14:47
*/


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eastwind.common.BaseContext;
import com.eastwind.common.Result;
import com.eastwind.entity.ShoppingCart;
import com.eastwind.service.ShoppingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    ShoppingService shoppingService;

    @DeleteMapping("/clean")
    public Result<String> delete(){
        // 获取当前用户id，根据用户id去ShoppingCart表里面删除所有数据即可
        Long currentId = BaseContext.getCurrentId();
        // 查询当前用户id的数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingService.remove(queryWrapper);
        return Result.success("成功清空购物车");
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // 使用数据库中的userId和当前登录的用户Id比较来查看购物车
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        // 将查询到的数据呈现在购物车中
        List<ShoppingCart> list = shoppingService.list(queryWrapper);
        return Result.success(list);
    }

    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info(shoppingCart.toString());
        // 得到当前用户的id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        // 获取当前菜品id(就是当前添加进来的菜品id)
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        // 判断添加的是菜品还是套餐
        if (dishId != null){
            // 如果菜品id不为空，说明传入的是菜品，反之是套餐
            // 并判断数据库中是否有菜品id
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }
        else{
            // 判断数据中是否有这个套餐id
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        // 查询菜品或套餐是否存在于购物车中
        ShoppingCart cart = shoppingService.getOne(queryWrapper);
        // 存在于购物车中
        if (cart != null){
            // 得到数量
            Integer number = cart.getNumber();
            cart.setNumber(number+1);
            shoppingService.updateById(cart);
        }else{
            // 不存在，就需要为当前购物车设置创建时间，因为它没有给默认值,并插入到数据库中
            shoppingCart.setCreateTime(LocalDateTime.now());
            // 不存在，就需要为当前购物车设置数量
            shoppingCart.setNumber(1);
            shoppingService.save(shoppingCart);
            cart = shoppingCart;
        }
        return Result.success(cart);
    }
}
