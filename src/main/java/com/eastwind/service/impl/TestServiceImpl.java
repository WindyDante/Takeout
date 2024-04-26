package com.eastwind.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl {

    @Value("${test.val}")
    String val;

    public void test(){
        System.out.println(val);
    }

}
