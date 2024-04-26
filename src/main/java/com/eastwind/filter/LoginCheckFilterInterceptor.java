package com.eastwind.filter;

/*
@author zhangJH
@create 2023-07-23-15:25
*/


import com.alibaba.fastjson.JSON;
import com.eastwind.common.BaseContext;
import com.eastwind.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// filterName是拦截器的名字，urlPatterns是拦截路径，/*是拦截所有请求
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilterInterceptor implements Filter {

    // 路径匹配
    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    // 判断该请求是否需要被处理
    public boolean check(String[] urls, String uri){
        for (String url : urls) {
            boolean match = ANT_PATH_MATCHER.match(url, uri);
            // 需要被处理
            if (match) {
                return true;
            }
        }
        // 遍历完后不需要被处理
        return false;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 获取本次请求的URI
        String uri = request.getRequestURI();
        log.info("拦截到路径{}",uri);

        // 定义不需要被拦截的请求
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

        boolean check = check(urls, uri);
        // 不需要被处理，放行并退出
        if (check) {
            log.info("路径{}不需要被处理",request.getRequestURI());
            filterChain.doFilter(request,response);
            return;
        }

        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));
            Long userId = (Long)request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }


        // 判断登录状态，如果已登录，放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录");

            // 根据session获取我们存的id值
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        // 在前端页面有一个拦截器，可以拦截数据，如果数据符合条件则放行，否则重定向回登录页面
        // 这个数据由我们进行发送
        // 需要导入fastJson的包来发送Json字符串
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));

        // request.getRequestURI()获取拦截路径
//        log.info("拦截到路径{}",request.getRequestURI());

    }
}
