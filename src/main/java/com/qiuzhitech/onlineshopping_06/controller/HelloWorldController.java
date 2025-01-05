package com.qiuzhitech.onlineshopping_06.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class HelloWorldController {
    @Resource
    FakeDependency dependency;

    public HelloWorldController(FakeDependency dependency) {
        this.dependency = dependency;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "helloWorld";
    }

    @ResponseBody
    @GetMapping("/echo/{path}")
    public String echo(@PathVariable("path") String path) {
        return "Hello World from path: " + path ;
    }

    public int addPlug2(int a, int b) {
        return dependency.add(a,b) + 2;
    }

}

// Command + shift + t