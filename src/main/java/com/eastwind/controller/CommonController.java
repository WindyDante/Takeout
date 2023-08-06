package com.eastwind.controller;

/*
@author zhangJH
@create 2023-07-26-14:03
*/

import com.eastwind.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${takeout.path}")
    private String basePath;

    @GetMapping("download")
    public void download(String name, HttpServletResponse httpServletResponse) {
        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            // 输入流，通过输入流读取文件内容
            fileInputStream = new FileInputStream(basePath + name);
            // 响应输出流，将数据输出到响应头是哪个
            outputStream = httpServletResponse.getOutputStream();
            // 响应类型是一个image
            httpServletResponse.setContentType("image/jpeg");
            int len;
            byte[] bytes = new byte[1024];
            // 每次读取1024个字节，并输出到响应头中
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                // 刷新流
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        // 输出文件
        log.info(file.toString());

        // 判断路径是否存在
        File dir = new File(basePath);
        if (!dir.exists()) {
            // 如果不存在
            dir.mkdirs();
        }

        // 获取传入的原文件名
        String originalFilename = file.getOriginalFilename();
        // 获取文件后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 为了防止出现重复的文件名，我们需要使用UUID
        String fileName = UUID.randomUUID() + suffix;

        // 设置文件转存
        try {
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.success(fileName);
    }
}
