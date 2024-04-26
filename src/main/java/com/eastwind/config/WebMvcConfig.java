package com.eastwind.config;


import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.eastwind.common.JacksonObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.List;

@Configuration
@Slf4j
@EnableSwagger2
@EnableKnife4j
public class WebMvcConfig implements WebMvcConfigurer {
// 注意，这里是实现WebMvcConfigurer，并实现extendMessageConverters方法才可以使用
    /**
     * 如果资源放在resources目录下，需要添加资源映射
     * */
//    @Override
//    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
//        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
//    }


    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转化器，底层使用jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将上面的消息转化器追加到mvc框架的转化器集合中，(index=0,表示在第一个设置，避免被其他转换器接收，从而达不到想要的功能)
        converters.add(0,messageConverter);
    }

    @Bean
    public Docket createRestApi() {
        //文档类型
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.eastwind.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("瑞吉外卖")
                .version("1.0")
                .description("瑞吉外卖接口文档")
                .build();
    }
}