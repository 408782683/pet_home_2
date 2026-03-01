package com.rain.service;

import com.alibaba.fastjson2.JSONObject;
import com.rain.mapper.OrderExMapper;

import java.sql.SQLException;

public class OrderServiceEx {
    private final OrderExMapper mapper = new OrderExMapper();

    public JSONObject findOrderListResult(Integer userId, Integer page, Integer size, String status) throws SQLException {
        JSONObject result = new JSONObject();
        result.put("orderList", mapper.findOrderList(userId, page, size, status));
        result.put("total", mapper.countOrder(userId, status));
        return result;
    }
}
