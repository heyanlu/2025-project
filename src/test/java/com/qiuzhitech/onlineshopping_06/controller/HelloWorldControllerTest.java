package com.qiuzhitech.onlineshopping_06.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@SpringBootTest
class HelloWorldControllerTest {
    @Resource
    private HelloWorldController helloWorldController;

    @Spy
    FakeDependency dependency;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testAddPlug2() {
        int result = helloWorldController.addPlug2(2,3);
        assertEquals(7,result);
    }

    @Test
    void testAddPlug2WithMock() {
        helloWorldController = new HelloWorldController(dependency);
        when(dependency.add(anyInt(),anyInt()))
                .thenReturn(100);
        int result = helloWorldController.addPlug2(2,3);
        assertEquals(102, result);
//  Write testable, secure application,
//  keep 85% code coverage with unit test via Junit and  use Mockito for dependency mock.
    }
}