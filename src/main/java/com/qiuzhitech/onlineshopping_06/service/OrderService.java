package com.qiuzhitech.onlineshopping_06.service;


import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_06.service.mq.RocketMQService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {
    @Resource
    OnlineShoppingOrderDao orderDao;

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    RedisService redisService;

    @Resource
    private RocketMQService rocketMQService;

    public OnlineShoppingOrder createOrder(String commodityId, String userId) {
        OnlineShoppingOrder order = OnlineShoppingOrder.builder()
                .orderNo(UUID.randomUUID().toString())
                .commodityId(Long.valueOf(commodityId))
                .userId(Long.valueOf(userId))
                .createTime(new Date())
                .orderAmount(1L)
                .orderStatus(1)
                // 0: invalid order
                // 1. pending payment
                // 2. finish payment
                // 99. overtime order
                .build();
        return order;
    }
    public OnlineShoppingOrder placeOrderOriginal(String commodityId, String userId) {
        OnlineShoppingCommodity commodityDetail = commodityDao.getCommodityDetail(Long.valueOf(commodityId));
        int availableStock = commodityDetail.getAvailableStock();
        int lockStock = commodityDetail.getLockStock();
        if (availableStock > 0) {
            availableStock--;
            lockStock++;
            commodityDetail.setAvailableStock(availableStock);
            commodityDetail.setLockStock(lockStock);
            commodityDao.updateCommodity(commodityDetail);
            OnlineShoppingOrder order = createOrder(commodityId, userId);
            orderDao.insertOrder(order);
            log.info("Place order successfully, current availableStock:" +  availableStock);
            return order;
        } else {
            log.warn("commodity out of stock, commodityId:" + commodityDetail.getCommodityId());
            return null;
        }
    }
    public OnlineShoppingOrder placeOrderOneSQL(String commodityId, String userId) {
        int result = commodityDao.deductStockWithCommodityId(Long.valueOf(commodityId));
        if (result > 0) {
            OnlineShoppingOrder order = createOrder(commodityId, userId);
            orderDao.insertOrder(order);
            log.info("Place order successfully, orderNum:" +  order.getOrderNo());
            return order;
        } else {
            log.warn("commodity out of stock, commodityId:" + commodityId);
            return null;
        }
    }

    public OnlineShoppingOrder placeOrderRedis(String commodityId, String userId) {
        String redisKey = "commodity:" + commodityId;
        long result = redisService.stockDeduct(redisKey);

        if (result >= 0) {
            OnlineShoppingOrder order = placeOrderOneSQL(commodityId, userId);
            log.info("Place order successfully, orderNum:" +  order.getOrderNo());
            return order;
        } else {
            log.warn("commodity out of stock, commodityId:" + commodityId);
            return null;
        }
    }

    public OnlineShoppingOrder placeOrderFinal(String commodityId, String userId) {
        if (redisService.isInDenyList(userId, commodityId)) {
            log.info("Each user have only one quote for this commodity");
            return null;
        }

        String redisKey = "commodity:" + commodityId;
        long result = redisService.stockDeduct(redisKey);
        // 0: invalid order
        // 1. pending payment
        // 2. finish payment
        // 99. overtime order
        if (result >= 0) {
            OnlineShoppingOrder order = createOrder(commodityId, userId);
            rocketMQService.sendMessage("createOrder", JSON.toJSONString(order));
            redisService.addToDenyList(userId, commodityId);
            return order;
        } else {
            log.warn("commodity out of stock, commodityId:" + commodityId);
            return null;
        }
    }

    public OnlineShoppingOrder placeOrderDistributedLock(String commodityId, String userId) {
        String redisKey = "commodityLock:" + commodityId;
        String requestId = UUID.randomUUID().toString();
        boolean result = redisService.tryToGetDistributedLock(redisKey, requestId, 5000);
        if (result) {
            OnlineShoppingOrder order = placeOrderOneSQL(commodityId, userId);
            redisService.releaseDistributedLock(redisKey, requestId);
            return order;
        } else {
            log.warn("processOrderRedis failed due to not fetching Lock, please try again latter, commodityId:" + commodityId);
            return null;
        }
    }

    public OnlineShoppingOrder queryOrderByOrderNum(String orderNum) {
        return orderDao.queryOrderByOrderNo(orderNum);
    }
    public void payOrder(String orderNum) {
        OnlineShoppingOrder order = queryOrderByOrderNum(orderNum);
        order.setOrderStatus(2);
        order.setPayTime(new Date());
        OnlineShoppingCommodity commodityDetail = commodityDao.getCommodityDetail(order.getCommodityId());
        commodityDetail.setLockStock(commodityDetail.getLockStock()-1);
        orderDao.updateOrder(order);
        commodityDao.updateCommodity(commodityDetail);
    }
}
