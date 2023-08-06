package com.eastwind.controller;

/*
@author zhangJH
@create 2023-07-28-14:38
*/


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eastwind.common.MailUtils;
import com.eastwind.common.Result;
import com.eastwind.entity.User;
import com.eastwind.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;



    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpSession session) {
        // 获取邮箱
        String phone = map.get("phone").toString();
        // 获得验证码，需要和系统内部的验证码进行比对
        String code = map.get("code").toString();
        // 把刚刚存入Redis的code拿出来
        Object codeInRedis = redisTemplate.opsForValue().get("code");
        // 判断从Redis中获取的code是否相同
        if (code != null && code.equals(codeInRedis)) {
            // 如果输入正确，验证用户是否存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                // 如果user为空，就创建一个新的user对象
                user = new User();
                user.setPhone(phone);
                userService.save(user);
                user.setName("用户" + codeInRedis);
            }
            // 存个session，表示登录状态
            session.setAttribute("user", user.getId());
            // 并作为结果返回
            // 如果登录成功，则删除Redis中的验证码
            redisTemplate.delete("code");
            return Result.success(user);
        }
        return Result.error("登录失败");
    }


    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpSession session) throws MessagingException {
        String phone = user.getPhone();
        if (!phone.isEmpty()) {
            //随机生成一个验证码
            String code = MailUtils.achieveCode();
            log.info(code);
            //这里的phone其实就是邮箱，code是我们生成的验证码
            MailUtils.sendTestMail(phone, code);
            // 存储到Redis中并设置5分钟的存活时间
            redisTemplate.opsForValue().set("code",code,5, TimeUnit.MINUTES);
            return Result.success("验证码发送成功");
        }
        return Result.error("验证码发送失败");
    }

}
