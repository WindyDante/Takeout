package com.eastwind.service.impl;

/*
@author zhangJH
@create 2023-07-29-14:02
*/


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eastwind.entity.AddressBook;
import com.eastwind.mapper.AddressBookMapper;
import com.eastwind.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
