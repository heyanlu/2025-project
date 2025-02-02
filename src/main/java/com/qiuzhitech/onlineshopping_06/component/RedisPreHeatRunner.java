package com.qiuzhitech.onlineshopping_06.component;

import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_06.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class RedisPreHeatRunner implements ApplicationRunner {
    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    RedisService redisService;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // read from MYSQL
        // write to Reids
        List<OnlineShoppingCommodity> onlineShoppingCommodities = commodityDao.listCommodities();
        for (OnlineShoppingCommodity commodity: onlineShoppingCommodities) {
            String redisKey = "commodity:" + commodity.getCommodityId();
            redisService.setValue(redisKey, commodity.getAvailableStock().toString());
            log.info("preHeat Staring: Initialize commodity :" + commodity.getCommodityId());
        }
    }
}
