package com.rain.mapper;

import com.alibaba.fastjson2.JSONObject;
import com.rain.util.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderExMapper {
    public List<JSONObject> findOrderList(Integer userId, Integer page, Integer size, String status) throws SQLException {
        StringBuilder sql = new StringBuilder("select * from orders where user_id=?");
        List<Object> params = new ArrayList<>(); params.add(userId);
        if (status != null && !status.isEmpty()) { sql.append(" and status=?"); params.add(status); }
        sql.append(" order by create_time desc limit ?,?"); params.add((page - 1) * size); params.add(size);
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            ResultSet rs = ps.executeQuery();
            List<JSONObject> list = new ArrayList<>();
            while (rs.next()) {
                JSONObject x = new JSONObject();
                x.put("id", rs.getInt("id")); x.put("orderNo", rs.getString("order_no")); x.put("totalAmount", rs.getBigDecimal("total_amount"));
                x.put("receiverName", rs.getString("receiver_name")); x.put("receiverPhone", rs.getString("receiver_phone")); x.put("receiverAddress", rs.getString("receiver_address"));
                x.put("status", rs.getString("status")); x.put("createTime", rs.getTimestamp("create_time"));
                x.put("orderDetails", findOrderDetail(rs.getInt("id"))); list.add(x);
            }
            return list;
        }
    }

    public Integer countOrder(Integer userId, String status) throws SQLException { StringBuilder sql = new StringBuilder("select count(*) num from orders where user_id=?"); List<Object> params = new ArrayList<>(); params.add(userId); if(status!=null&&!status.isEmpty()){sql.append(" and status=?");params.add(status);} try(Connection conn=JdbcUtil.getConnection(); PreparedStatement ps=conn.prepareStatement(sql.toString())){for(int i=0;i<params.size();i++) ps.setObject(i+1,params.get(i)); ResultSet rs=ps.executeQuery(); return rs.next()?rs.getInt("num"):0;}}

    private List<JSONObject> findOrderDetail(Integer orderId) throws SQLException { try(Connection conn=JdbcUtil.getConnection(); PreparedStatement ps=conn.prepareStatement("select * from order_detail where order_id=?")){ps.setInt(1,orderId); ResultSet rs=ps.executeQuery(); List<JSONObject> list=new ArrayList<>(); while(rs.next()){JSONObject d=new JSONObject(); d.put("id",rs.getInt("id")); d.put("productId",rs.getInt("product_id")); d.put("productName",rs.getString("product_name")); d.put("productImage",rs.getString("product_image")); d.put("price",rs.getBigDecimal("price")); d.put("quantity",rs.getInt("quantity")); list.add(d);} return list; }}
}
