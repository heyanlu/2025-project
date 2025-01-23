package com.qiuzhitech.onlineshopping_06.db.dao.impl;

import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingCommodity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class OnlineShoppingCommodityDaoImplTest {
    @Resource
    private OnlineShoppingCommodityDao onlineShoppingCommodityDao;

    @BeforeEach
    void setUp() {
    }

    @Test
    void insertCommodity() {
        OnlineShoppingCommodity onlineShoppingCommodity =  OnlineShoppingCommodity.builder()
                .commodityName("TestCommodity2")
                .commodityDesc("desc2")
                .availableStock(1112)
                .totalStock(1112)
                .price(9992)
                .lockStock(0)
                .creatorUserId(124L)
                .build();
        onlineShoppingCommodityDao.insertCommodity(onlineShoppingCommodity);
    }

    @Test
    void listCommodities() {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = onlineShoppingCommodityDao.listCommodities();
        log.info(onlineShoppingCommodities.toString());
    }
}
