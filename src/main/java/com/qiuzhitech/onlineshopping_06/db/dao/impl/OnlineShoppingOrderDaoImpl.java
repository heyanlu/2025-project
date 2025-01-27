package com.qiuzhitech.onlineshopping_06.db.dao.impl;

import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping_06.db.mappers.OnlineShoppingOrderMapper;
import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingOrder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class OnlineShoppingOrderDaoImpl implements OnlineShoppingOrderDao {
    @Resource
    OnlineShoppingOrderMapper orderMapper;

    @Override
    public int insertOrder(OnlineShoppingOrder order) {
        return orderMapper.insert(order);
    }

    @Override
    public OnlineShoppingOrder queryOrderByOrderNo(String orderNum) {
        return orderMapper.queryOrderByOrderNo(orderNum);
    }

    @Override
    public int updateOrder(OnlineShoppingOrder order) {
        return orderMapper.updateByPrimaryKey(order);
    }
}
