package com.eastwind.controller;

/*
@author zhangJH
@create 2023-07-27-8:36
*/


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eastwind.common.Result;
import com.eastwind.entity.Category;
import com.eastwind.entity.Dish;
import com.eastwind.entity.DishDto;
import com.eastwind.entity.DishFlavor;
import com.eastwind.service.CategoryService;
import com.eastwind.service.DisFlavorService;
import com.eastwind.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 菜品管理
 * */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DisFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

//    @GetMapping("/list")
//    public Result<List<Dish>> list(Dish dish){
//        // Dish泛型在LambdaQueryWrapper中的作用是指定查询目标实体类类型的内容
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        // 比较传入的菜品id和数据库中的id是否相同
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        // 查询该菜品的启用情况，启用时，才会展示
//        queryWrapper.eq(Dish::getStatus,1);
//        // 先用sort升序排序，sort相同的情况下，对更新时间降序排序
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        return Result.success(list);
//    }

    @GetMapping("/list")
    // 先将返回值类型改为List<DishDto>
    public Result<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        // 得到该菜品项对应的菜品
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());

        // 添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件(先按照sort来排序，如果sort相同，再按照更新时间来排序)
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            // 分类id
            Long categoryId = item.getCategoryId();
            // 根据Id查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null){
                // 如果分类对象查询到了，说明该菜品有分类
                String categoryName = category.getName();
                // 就让菜品设置一下这个分类对象名字
                dishDto.setCategoryName(categoryName);
            }

            // 得到菜品的id
            Long itemId = item.getId();

            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            // 查找与当前菜品id相同的口味信息
            wrapper.eq(DishFlavor::getDishId,itemId);
            List<DishFlavor> flavors = dishFlavorService.list(wrapper);
            // 设置菜品口味
            dishDto.setFlavors(flavors);
            return dishDto;
        }).collect(Collectors.toList());
        return Result.success(dishDtoList);
    }

    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id){
        // 通过在dishService中封装的getByIdWithFlavor方法来得到DishDto对象
        // 也就是得到一个新的菜品对象，返回给页面回显数据
        DishDto idWithFlavor = dishService.getByIdWithFlavor(id);
        return Result.success(idWithFlavor);
    }

    @GetMapping("/page")
    public Result<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        //这个就是我们到时候返回的结果
        Page<DishDto> dishDtoPage = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.like(name != null, Dish::getName, name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo, queryWrapper);

        //对象拷贝，将pageInfo对象中的所有属性，对应的拷贝到dishDtoPage中
        // 并且忽略records集合中的数据，为什么忽悠掉呢，因为records中的数据是展示列表上的所有数据
        // 我们想让这个records中的数据来更新一下，然后赋值给新的分页对象，所以先忽略
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        //获取原records数据
        List<Dish> records = pageInfo.getRecords();

        //遍历每一条records数据
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            //将数据赋给dishDto对象，这里面得到了records中的每一个Dish对象
            // 然后赋值给dishDto
            BeanUtils.copyProperties(item, dishDto);

            //然后获取一下dish对象的category_id属性
            // 得到dish对象的id
            Long categoryId = item.getCategoryId();  //分类id

            //根据这个id属性，获取到Category对象（这里需要用@Autowired注入一个CategoryService对象）
            Category category = categoryService.getById(categoryId);

            // 刚开始时，有些数据是没有绑定菜品分类，就会出错
            if (category != null) {
                //随后获取Category对象的name属性，也就是菜品分类名称
                // 得到id属性，就可以根据id查询对象，并且获得对象的名字
                String categoryName = category.getName();

                //最后将菜品分类名称赋给dishDto对象就好了
                // 最后把对象的菜品分类名称赋值给disDto对象即可
                dishDto.setCategoryName(categoryName);

            }

            //结果返回一个dishDto对象
            return dishDto;

            //并将dishDto对象封装成一个集合，作为我们的最终结果，这个集合，就是要展示的新数据
        }).collect(Collectors.toList());

        // 最后把新数据，设置到这个新的分页上
        dishDtoPage.setRecords(list);
        return Result.success(dishDtoPage);
    }


    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        dishService.saveWithFlavor(dishDto);
        return Result.success("新增菜品成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        dishService.updateWithFlavor(dishDto);
        return Result.success("更新菜品成功");
    }


}
