package com.rain.mapper;
import com.rain.entity.Review;
import com.rain.util.JdbcUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ReviewMapper {
    //分页展示商品对应的评论数据
    public List<Review> getReviewByPid(Integer pid ,Integer pageNum,Integer pageSize) throws SQLException {
        String sql = "select * from review where product_id = ? limit ? ,?";
        try(Connection conn = JdbcUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //处理参数
            pstmt.setInt(1,pid);
            pstmt.setInt(2,(pageNum-1)*pageSize);
            pstmt.setInt(3,pageSize);
            //执行sql
            ResultSet rs = pstmt.executeQuery();
            //创建一个集合用于存储评论数据
            List<Review> reviews = new ArrayList<>();
            while (rs.next()){
                //创建review对象
                Review review = new Review();
                review.setId(rs.getInt("id"));
                review.setOrderId(rs.getInt("order_id"));
                review.setUserId(rs.getInt("user_id"));;
                review.setProductId(pid);
                review.setContent(rs.getString("content"));
                review.setRating(rs.getInt("rating"));
                review.setCreateTime(rs.getDate("create_time"));
                //存入集合
                reviews.add(review);
            }
            //返回集合
            return reviews;
        }
    }

    //根据pid查询评论的数量
    public Integer findCountByPid(Integer pid) throws SQLException {
        String sql = "select count(*) num from review where product_id = ?";
        try(Connection conn = JdbcUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            //处理参数
            pstmt.setInt(1,pid);
            //执行sql
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                return rs.getInt("num");
            }else{
                return 0;
            }
        }
    }



    public List<Review> findReviewListByUserId(Integer userId) throws SQLException {
        String sql = "select r.*, od.product_name, od.product_image, o.order_no " +
                "from review r " +
                "left join order_detail od on r.order_id = od.order_id and r.product_id = od.product_id " +
                "left join orders o on r.order_id = o.id " +
                "where r.user_id = ? order by r.create_time desc";
        try(Connection conn = JdbcUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,userId);
            ResultSet rs = pstmt.executeQuery();
            List<Review> list = new ArrayList<>();
            while (rs.next()) {
                Review review = new Review();
                review.setId(rs.getInt("id"));
                review.setOrderId(rs.getInt("order_id"));
                review.setUserId(rs.getInt("user_id"));
                review.setProductId(rs.getInt("product_id"));
                review.setContent(rs.getString("content"));
                review.setRating(rs.getInt("rating"));
                review.setCreateTime(rs.getDate("create_time"));
                review.setProductName(rs.getString("product_name"));
                review.setProductImage(rs.getString("product_image"));
                review.setOrderNo(rs.getString("order_no"));
                list.add(review);
            }
            return list;
        }
    }

    public boolean existsByUserOrderProduct(Integer userId, Integer orderId, Integer productId) throws SQLException {
        String sql = "select count(*) num from review where user_id = ? and order_id = ? and product_id = ?";
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, orderId);
            pstmt.setInt(3, productId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt("num") > 0;
        }
    }

    public void addReview(Review review) throws SQLException {
        String sql = "insert into review(order_id, user_id, product_id, content, rating, create_time) values(?,?,?,?,?,now())";
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, review.getOrderId());
            pstmt.setInt(2, review.getUserId());
            pstmt.setInt(3, review.getProductId());
            pstmt.setString(4, review.getContent());
            pstmt.setInt(5, review.getRating());
            pstmt.executeUpdate();
        }
    }

    public int updateReview(Review review) throws SQLException {
        String sql = "update review set content = ?, rating = ? where id = ? and user_id = ?";
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, review.getContent());
            pstmt.setInt(2, review.getRating());
            pstmt.setInt(3, review.getId());
            pstmt.setInt(4, review.getUserId());
            return pstmt.executeUpdate();
        }
    }

    public int deleteReview(Integer id, Integer userId) throws SQLException {
        String sql = "delete from review where id = ? and user_id = ?";
        try (Connection conn = JdbcUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate();
        }
    }
}
