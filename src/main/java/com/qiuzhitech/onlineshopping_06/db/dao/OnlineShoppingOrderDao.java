package com.qiuzhitech.onlineshopping_06.db.dao;

import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingOrder;

public interface OnlineShoppingOrderDao {
    int insertOrder(OnlineShoppingOrder order);
    OnlineShoppingOrder queryOrderByOrderNo(String orderNum);

    int updateOrder(OnlineShoppingOrder order);
}
