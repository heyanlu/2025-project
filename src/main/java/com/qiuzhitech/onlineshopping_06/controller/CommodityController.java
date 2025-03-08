package com.qiuzhitech.onlineshopping_06.controller;

import com.qiuzhitech.onlineshopping_06.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping_06.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping_06.service.EsService;
import com.qiuzhitech.onlineshopping_06.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class CommodityController {
    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    SearchService searchService;

    @Resource
    EsService esService;

    @RequestMapping("/addItem")
    public String addCommodity() {
        return "add_commodity";
    }
    @PostMapping("commodities")
    public String addCommodity(@RequestParam("commodityId") long commodityId,
                               @RequestParam("commodityName") String commodityName,
                               @RequestParam("commodityDesc") String commodityDesc,
                               @RequestParam("price") int price,
                               @RequestParam("availableStock") int availableStock,
                               @RequestParam("creatorUserId") long creatorUserId,
                               Map<String, Object> resultMap) throws IOException {
        OnlineShoppingCommodity commodity = OnlineShoppingCommodity.builder()
                .commodityId(commodityId)
                .commodityName(commodityName)
                .commodityDesc(commodityDesc)
                .price(price)
                .availableStock(availableStock)
                .creatorUserId(creatorUserId)
                .totalStock(availableStock)
                .lockStock(0)
                .build();
        commodityDao.insertCommodity(commodity);
        esService.addCommodity(commodity);
        resultMap.put("Item", commodity);
        return "add_commodity_success";
    }

    @GetMapping("searchAction")
    public String searchAction(@RequestParam("keyWord") String keyword,
                               Map<String, Object> resultMap) {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = searchService.searchCommodityWithEs(keyword);
        resultMap.put("itemList", onlineShoppingCommodities);
        return "search_items";
    }

    @GetMapping("/commodities/{sellerId}")
    public String listCommoditiesBySellerId(@PathVariable("sellerId") Long sellerId,
                                            Map<String, Object> resultMap) {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = commodityDao.listCommoditiesByUserId(sellerId);
        resultMap.put("itemList", onlineShoppingCommodities);
        return "list_items";
    }
    @GetMapping({"/commodities","/"})
    public String listCommodities(Map<String, Object> resultMap) {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = commodityDao.listCommodities();
        resultMap.put("itemList", onlineShoppingCommodities);
        return "list_items";
    }

    @GetMapping({"/item/{commodityId}"})
    public String getCommodity(@PathVariable("commodityId") Long commodityId,
                               Map<String, Object> resultMap) {
        OnlineShoppingCommodity commodity = commodityDao.getCommodityDetail(commodityId);
        resultMap.put("commodity", commodity);
        return "item_detail";
    }

}
