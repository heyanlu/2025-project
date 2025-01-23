package com.qiuzhitech.onlineshopping_06.db.dao;

import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityDao {
    int insertCommodity(OnlineShoppingCommodity commodity);
    List<OnlineShoppingCommodity> listCommodities();
}
