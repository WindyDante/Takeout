package com.eastwind.mapper;

/*
@author zhangJH
@create 2023-07-29-14:01
*/


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eastwind.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
