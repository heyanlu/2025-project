package com.qiuzhitech.onlineshopping_06.controller;

import org.springframework.stereotype.Service;

@Service
public class FakeDependency {
    public int add(int a, int b) { return (a + b); }
}
