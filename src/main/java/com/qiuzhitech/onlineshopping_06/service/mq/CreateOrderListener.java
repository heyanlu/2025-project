package com.qiuzhitech.onlineshopping_06.service.mq;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_06.service.OrderService;
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
@RocketMQMessageListener(topic = "createOrder", consumerGroup = "createOrderGroup")
public class CreateOrderListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    @Resource
    private OnlineShoppingCommodityDao onlineShoppingCommodityDao;
    @Resource
    private OnlineShoppingOrderDao onlineShoppingOrderDao;
    @Resource
    private RocketMQService rocketMQService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String orderBody = new String(messageExt.getBody());
        log.info("Create order body: " + orderBody);
        OnlineShoppingOrder order =
                JSON.parseObject(orderBody, OnlineShoppingOrder.class);
        int result = onlineShoppingCommodityDao.deductStockWithCommodityId(order.getCommodityId());
        if (result > 0) {
            // 0: invalid order
            // 1. pending payment
            // 2. finish payment
            // 99. overtime order
            onlineShoppingOrderDao.insertOrder(order);
            rocketMQService.sendDelayMessage("paymentCheck", JSON.toJSONString(order), 4);

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
