package com.qiuzhitech.onlineshopping_06.service.mq;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_06.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@RocketMQMessageListener(topic = "paymentCheck", consumerGroup = "paymentCheckGroup")
public class PaymentCheckListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    @Resource
    private OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    private OnlineShoppingOrderDao onlineShoppingOrderDao;
    @Autowired
    private RedisService redisService;

    @Override
    public void onMessage(MessageExt messageExt) {
        // 0: invalid order
        // 1. pending payment
        // 2. finish payment
        // 99. overtime order
        String orderBody = new String(messageExt.getBody());
        log.info("Create payment check body: " + orderBody);
        OnlineShoppingOrder orderMSG =
                JSON.parseObject(orderBody, OnlineShoppingOrder.class);
        OnlineShoppingOrder order = onlineShoppingOrderDao.queryOrderByOrderNo(orderMSG.getOrderNo());
        if (order == null) {
            log.error("Can't find order in database");
            throw new RuntimeException("Can't find order in database");
        }
        if (order.getOrderStatus() != 2)  {
            log.info("Didn't pay the order on time, order number：" + order.getOrderNo());
            // set stock +1,
            order.setOrderStatus(99);
            onlineShoppingOrderDao.updateOrder(order);
            onlineShoppingCommodityDao.revertStockWithCommodityId(order.getCommodityId());
            String redisKey = "commodity:"  + order.getCommodityId();
            redisService.revertStock(redisKey);
            redisService.removeFromDenyList(order.getUserId().toString(), order.getCommodityId().toString());
        } else {
            log.info("Skip order check since it is already marked finished" + JSON.toJSONString(order));
        }
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        // 在此方法中可以设置一些消费者的属性
        consumer.setMaxReconsumeTimes(2);  // 设置最大重试次数
        consumer.setConsumeTimeout(5000); // 设置消费超时时间为5秒
        consumer.setConsumeThreadMin(1);
        consumer.setConsumeThreadMax(1);
    }
}
