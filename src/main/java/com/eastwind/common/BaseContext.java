package com.eastwind.common;

/*
@author zhangJH
@create 2023-07-25-9:22
*/



/**
 * 基于ThreadLocal的工具类
 * */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();

    // 设置id
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    // 获取id
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
