package com.qiuzhitech.onlineshopping_06.config;

import com.qiuzhitech.onlineshopping_06.model.UserDemo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Configuration
public class UserConfig {

    @Bean(name = "zhangsan")
    public UserDemo userZhangSanProvider() {
        return new UserDemo(0, "zhangSan", "zhangSan@xxx.com");
    }

    @Bean(name = "lisi")
    public UserDemo userLisiDemoProvider() {
        return new UserDemo(0, "lisi", "lisi@xxx.com");
    }
}
