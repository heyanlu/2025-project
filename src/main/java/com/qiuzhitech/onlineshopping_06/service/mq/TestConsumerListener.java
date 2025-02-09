package com.qiuzhitech.onlineshopping_06.service.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.LockSupport;

@Component
@Slf4j
@RocketMQMessageListener(topic = "consumerTopic", consumerGroup = "consumerGroup")
public class TestConsumerListener implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody());
        if (messageExt.getReconsumeTimes() <=2) {
            long nanos = 5_000_000_000L; // 4 seconds in nanoseconds
            LockSupport.parkNanos(nanos);
            log.info("Received message: " + message);
            throw new RuntimeException("Test");
        }
        else { // send it to DLQ
            log.info("this is " + messageExt.getReconsumeTimes() + " times");
            log.info("Received message: " + message);
        }
    }
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        // 在此方法中可以设置一些消费者的属性
        consumer.setMaxReconsumeTimes(2);  // 设置最大重试次数
        consumer.setConsumeTimeout(3000); // 设置消费超时时间为5秒
        consumer.setConsumeThreadMin(1);
        consumer.setConsumeThreadMax(1);
    }
}
