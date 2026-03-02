package com.rain.mapper;
import com.rain.entity.Cart;
import com.rain.util.JdbcUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class CartMapper {
    //添加购物车
    public void addToCart(Integer productId,Integer userId,Integer quantity) throws SQLException {
        String sql = "insert into cart(product_id,user_id,quantity,create_time) " +
                "values(?,?,?,now())";
        try(Connection conn = JdbcUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //处理参数
            pstmt.setInt(1,productId);
            pstmt.setInt(2,userId);
            pstmt.setInt(3,quantity);
            //执行sql
            pstmt.executeUpdate();
        }
    }

    // 判断该商品是否被当前用户所添加
    public boolean idAddCart(Integer productId,Integer userId) throws SQLException {
        String sql = "select * from cart where product_id =  ? and user_id = ?";
        try(Connection conn = JdbcUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //处理参数
            pstmt.setInt(1,productId);
            pstmt.setInt(2,userId);
            //执行sql
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    // 修改购物车中数量
    public void updateCartQuantity(Integer productId,Integer userId,Integer quantity) throws SQLException {
        String sql = "update cart set quantity = quantity + ? " +
                "where product_id = ? and user_id = ?";
        try(Connection conn = JdbcUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //处理参数
            pstmt.setInt(1,quantity);
            pstmt.setInt(2,productId);
            pstmt.setInt(3,userId);
            //执行sql
            pstmt.executeUpdate();
        }
    }

    // 按购物车ID修改数量
    public int updateCartQuantityById(Integer id, Integer userId, Integer quantity) throws SQLException {
        String sql = "update cart set quantity = ? where id = ? and user_id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, id);
            pstmt.setInt(3, userId);
            return pstmt.executeUpdate();
        }
    }

    //根据id来删除购物车数据
    public void deleteById(Integer id,Connection conn) throws SQLException {
        String sql = "delete from cart where id = ?";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //处理参数
            pstmt.setLong(1,id);
            //执行sql
            pstmt.executeUpdate();
        }
    }

    public int deleteByIdAndUserId(Integer id, Integer userId) throws SQLException {
        String sql = "delete from cart where id = ? and user_id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate();
        }
    }

    public int clearByUserId(Integer userId) throws SQLException {
        String sql = "delete from cart where user_id = ?";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate();
        }
    }


    // 根据用户id查询购物车列表（包含商品信息）
    public List<Cart> findCartListByUserId(Integer userId) throws SQLException {
        String sql = "select c.id,c.product_id,c.user_id,c.quantity,c.create_time,p.name,p.price,p.images as product_images from cart c left join product p on c.product_id=p.id where c.user_id=? order by c.create_time desc";
        try(Connection conn = JdbcUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,userId);
            ResultSet rs = pstmt.executeQuery();
            List<Cart> list = new ArrayList<>();
            while (rs.next()) {
                Cart cart = new Cart();
                cart.setId(rs.getInt("id"));
                cart.setProductId(rs.getInt("product_id"));
                cart.setUserId(rs.getInt("user_id"));
                cart.setQuantity(rs.getInt("quantity"));
                cart.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                cart.setProductName(rs.getString("name"));
                cart.setProductPrice(rs.getBigDecimal("price"));
                cart.setProductImages(rs.getString("product_images"));
                list.add(cart);
            }
            return list;
        }
    }

}
