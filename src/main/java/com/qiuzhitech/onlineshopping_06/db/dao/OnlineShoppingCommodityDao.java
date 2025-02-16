package com.qiuzhitech.onlineshopping_06.db.dao;

import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityDao {
    int insertCommodity(OnlineShoppingCommodity commodity);
    List<OnlineShoppingCommodity> listCommodities();

    List<OnlineShoppingCommodity> listCommoditiesByUserId(Long sellerId);
    OnlineShoppingCommodity getCommodityDetail(Long commodityId);
    int updateCommodity(OnlineShoppingCommodity commodityDetail);
    int deductStockWithCommodityId(Long commodityId);

    int revertStockWithCommodityId(Long commodityId);
}
