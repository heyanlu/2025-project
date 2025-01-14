package com.qiuzhitech.onlineshopping_06.controller;

import com.qiuzhitech.onlineshopping_06.model.UserDemo;
import com.qiuzhitech.onlineshopping_06.service.JwtService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserTestController {
    Map<Integer, UserDemo> users = new HashMap<>();

    @Resource(name = "zhangsan")
    UserDemo defaultUSer;

    @Resource
    JwtService jwtService;

    // create
    @ResponseBody
    @PostMapping("/users")
    public String createUser(@RequestParam("id") int id,
                             @RequestParam("name") String name,
                             @RequestParam("email") String email) {
        UserDemo userDemo = UserDemo.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();

        users.put(id, userDemo);
        System.out.println(userDemo.getName());
        System.out.println(userDemo.getEmail());
        return "success"; // view
    }

// get
    @GetMapping("/users/{id}")
    public String getUser(@PathVariable("id") int id,
                          Map<String, Object> resultMap) {
        UserDemo userDemo = users.getOrDefault(id, defaultUSer);
        resultMap.put("user", userDemo);
        String jwt = jwtService.generateToken(userDemo);
        resultMap.put("jwtToken", jwt);
        String jwtUserName = jwtService.extractUsername(jwt);
        resultMap.put("jwtUserName", jwtUserName);
        return "user_detail";
    }
}
