package com.qiuzhitech.onlineshopping_06;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.qiuzhitech.onlineshopping_06.db.mappers")
public class OnlineShopping06Application {

    public static void main(String[] args) {
        SpringApplication.run(OnlineShopping06Application.class, args);
    }

}
