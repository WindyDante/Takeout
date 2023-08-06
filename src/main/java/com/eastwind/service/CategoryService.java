package com.eastwind.service;

/*
@author zhangJH
@create 2023-07-25-11:08
*/


import com.baomidou.mybatisplus.extension.service.IService;
import com.eastwind.entity.Category;

public interface CategoryService extends IService<Category> {
    void delete(Long id);
}
