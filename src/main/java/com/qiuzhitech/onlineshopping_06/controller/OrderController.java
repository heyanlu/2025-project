package com.qiuzhitech.onlineshopping_06.controller;

import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping_06.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Map;

@Controller
public class OrderController {
    @Resource
    OrderService orderService;

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @RequestMapping("commodity/buy/{userId}/{commodityId}")
    public String buyCommodity(@PathVariable("userId") String userId,
                               @PathVariable("commodityId") String commodityId,
                               Map<String, Object> resultMap) {
//        OnlineShoppingOrder order = orderService.placeOrderOriginal(commodityId, userId);
//        OnlineShoppingOrder order = orderService.placeOrderOneSQL(commodityId, userId);
//        OnlineShoppingOrder order = orderService.placeOrderRedis(commodityId, userId);
//        OnlineShoppingOrder order = orderService.placeOrderDistributedLock(commodityId, userId);
        OnlineShoppingOrder order = orderService.placeOrderFinal(commodityId, userId);

        if (order != null) {
            resultMap.put("resultInfo", "Create Order " + order.getOrderNo() + " successfully!");
            resultMap.put("orderNo", order.getOrderNo());
        } else {
            resultMap.put("resultInfo", "Order create failed, check log for detail");
            resultMap.put("orderNo", "");
        }
        return "order_result";
    }

    @RequestMapping("commodity/orderQuery/{orderNum}")
    public String orderQuery(@PathVariable("orderNum") String orderNum,
                             Map<String, Object> resultMap) {
        OnlineShoppingOrder onlineShoppingOrder = orderService.queryOrderByOrderNum(orderNum);
        resultMap.put("order", onlineShoppingOrder);
        Long commodityId = onlineShoppingOrder.getCommodityId();
        OnlineShoppingCommodity commodityDetail = commodityDao.getCommodityDetail(commodityId);
        resultMap.put("commodity", commodityDetail);
        return "order_check";
    }

    @GetMapping("/commodity/payOrder/{orderNum}")
    public String payOrder(@PathVariable("orderNum") String orderNum,
                           Map<String, Object> resultMap) {
        orderService.payOrder(orderNum);
        return orderQuery(orderNum, resultMap);
    }
}
