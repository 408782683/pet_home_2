package com.rain.service;
import com.rain.entity.Review;
import com.rain.mapper.ReviewMapper;
import java.sql.SQLException;
import java.util.List;
public class ReviewService {
    private ReviewMapper reviewMapper = new ReviewMapper();

    //分页查询商品对应的评论数据
    public List<Review> findReviewByPid(Integer pid, Integer pageNum,Integer pageSize) throws SQLException {
        return reviewMapper.getReviewByPid(pid,pageNum,pageSize);
    }

    //查询商品对应的评论数量
    public Integer findCountByPid(Integer pid) throws SQLException {
        return reviewMapper.findCountByPid(pid);
    }



    public List<Review> findReviewListByUserId(Integer userId) throws SQLException {
        return reviewMapper.findReviewListByUserId(userId);
    }

    public boolean existsByUserOrderProduct(Integer userId, Integer orderId, Integer productId) throws SQLException {
        return reviewMapper.existsByUserOrderProduct(userId, orderId, productId);
    }

    public void addReview(Review review) throws SQLException {
        reviewMapper.addReview(review);
    }

    public boolean updateReview(Review review) throws SQLException {
        return reviewMapper.updateReview(review) > 0;
    }

    public boolean deleteReview(Integer id, Integer userId) throws SQLException {
        return reviewMapper.deleteReview(id, userId) > 0;
    }
}
