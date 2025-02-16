package com.qiuzhitech.onlineshopping_06.db.dao.impl;

import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_06.db.mappers.OnlineShoppingCommodityMapper;
import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingCommodity;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class OnlineShoppingCommodityDaoImpl implements OnlineShoppingCommodityDao {

    @Resource
    OnlineShoppingCommodityMapper onlineShoppingCommodityMapper;

    @Override
    public int insertCommodity(OnlineShoppingCommodity commodity) {
        return onlineShoppingCommodityMapper.insert(commodity);
    }

    @Override
    public List<OnlineShoppingCommodity> listCommodities() {
        return  onlineShoppingCommodityMapper.listCommodities();
    }

    @Override
    public List<OnlineShoppingCommodity> listCommoditiesByUserId(Long sellerId) {
        return onlineShoppingCommodityMapper.listCommoditiesByUserId(sellerId);
    }

    @Override
    public OnlineShoppingCommodity getCommodityDetail(Long commodityId) {
        return onlineShoppingCommodityMapper.selectByPrimaryKey(commodityId);
    }

    @Override
    public int updateCommodity(OnlineShoppingCommodity commodityDetail) {
        return onlineShoppingCommodityMapper.updateByPrimaryKey(commodityDetail);
    }

    @Override
    public int deductStockWithCommodityId(Long commodityId) {
        return onlineShoppingCommodityMapper.deductStockWithCommodityId(commodityId);
    }

    @Override
    public int revertStockWithCommodityId(Long commodityId) {
        return onlineShoppingCommodityMapper.revertStockWithCommodityId(commodityId);
    }
}
