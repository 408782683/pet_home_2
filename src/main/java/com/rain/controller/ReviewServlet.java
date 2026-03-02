package com.rain.controller;
import com.alibaba.fastjson2.JSONObject;
import com.rain.entity.Review;
import com.rain.service.ReviewService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
@WebServlet("/api/review/*")
public class ReviewServlet extends BaseServlet{
    private ReviewService reviewService = new ReviewService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if("/findByPid".equals(pathInfo)){
            //根据商品id查询对应的评论数据
            findByPid(req,resp);
        }else if("/findReviewList".equals(pathInfo)){
            findReviewList(req,resp);
        }else if("/add".equals(pathInfo)){
            addReview(req, resp);
        }else if("/update".equals(pathInfo)){
            updateReview(req, resp);
        }else if("/delete".equals(pathInfo)){
            deleteReview(req, resp);
        }else{
            writeJson(resp,error("接口不存在!!!!"));
        }
    }

    // 分页查询商品对应的评论数据
    private void findByPid(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //商品的id
        Integer pid = Integer.parseInt(req.getParameter("pid"));
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        // 当前页
        Integer page = params.getIntValue("page");
        // 每页展示多少条评论
        Integer size = params.getIntValue("size");
        try {
            // 分页查询
            List<Review> reviewList = reviewService.findReviewByPid(pid, page, size);
            //评论的数量
            Integer total = reviewService.findCountByPid(pid);
            JSONObject result = new JSONObject();
            result.put("total",total);
            result.put("reviewList",reviewList);
            writeJson(resp,success(result));
        } catch (SQLException e) {
            e.printStackTrace();
            writeJson(resp,error("系统繁忙,请稍后尝试"));
        }
    }



    private void findReviewList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        Integer userId = params.getIntValue("userId");
        try {
            List<Review> reviewList = reviewService.findReviewListByUserId(userId);
            JSONObject result = new JSONObject();
            result.put("reviewList",reviewList);
            writeJson(resp,success(result));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp,error("系统繁忙,请稍后尝试"));
        }
    }

    private void addReview(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        Review review = params.toJavaObject(Review.class);
        if (review.getUserId() == null || review.getOrderId() == null || review.getProductId() == null
                || review.getRating() == null || review.getContent() == null || review.getContent().trim().isEmpty()) {
            writeJson(resp, error("参数不完整"));
            return;
        }
        try {
            boolean exists = reviewService.existsByUserOrderProduct(review.getUserId(), review.getOrderId(), review.getProductId());
            if (exists) {
                writeJson(resp, error("该商品已评价"));
                return;
            }
            reviewService.addReview(review);
            writeJson(resp, success("评价成功"));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, error("评价失败"));
        }
    }

    private void updateReview(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        Review review = params.toJavaObject(Review.class);
        if (review.getId() == null || review.getUserId() == null || review.getRating() == null || review.getContent() == null || review.getContent().trim().isEmpty()) {
            writeJson(resp, error("参数不完整"));
            return;
        }
        try {
            boolean ok = reviewService.updateReview(review);
            writeJson(resp, ok ? success("修改成功") : error("未找到评价或无权限"));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, error("修改失败"));
        }
    }

    private void deleteReview(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        Integer id = params.getInteger("id");
        Integer userId = params.getInteger("userId");
        if (id == null || userId == null) {
            writeJson(resp, error("参数不完整"));
            return;
        }
        try {
            boolean ok = reviewService.deleteReview(id, userId);
            writeJson(resp, ok ? success("删除成功") : error("未找到评价或无权限"));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, error("删除失败"));
        }
    }
}
