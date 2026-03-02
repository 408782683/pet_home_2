package com.rain.controller;
import com.alibaba.fastjson2.JSONObject;
import com.rain.entity.Cart;
import com.rain.service.CartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
//购物车模块
@WebServlet("/api/cart/*")
public class CartServlet extends BaseServlet{
    private CartService cartService = new CartService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if("/addToCart".equals(pathInfo)){
            //添加购物车
            addToCart(req,resp);
        }else if("/findCartList".equals(pathInfo)){
            // 根据用户id加载对应的购物车数据
           findCartList(req,resp);
        }else if("/updateQuantity".equals(pathInfo)){
            updateQuantity(req, resp);
        }else if("/deleteItem".equals(pathInfo)){
            deleteItem(req, resp);
        }else if("/batchDelete".equals(pathInfo)){
            batchDelete(req, resp);
        }else if("/clear".equals(pathInfo)){
            clear(req, resp);
        }else{
            writeJson(resp,error("接口不存在!!!"));
        }
    }

    //添加购物车
    private void addToCart(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        // 商品id
        int productId = params.getIntValue("productId");
        // 用户id
        int userId = params.getIntValue("userId");
        // 数量
        int quantity = params.getIntValue("quantity");
        try {
            // 添加购物车
            cartService.addToCart(productId,userId,quantity);
            writeJson(resp,success("添加购物车成功"));
        } catch (SQLException e) {
            e.printStackTrace();
            writeJson(resp,error("系统繁忙,请稍后尝试"));
        }
    }



    private void findCartList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userIdStr = req.getParameter("userId");
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            writeJson(resp, error("缺少用户ID"));
            return;
        }

        Integer userId;
        try {
            userId = Integer.parseInt(userIdStr);
        } catch (NumberFormatException e) {
            writeJson(resp, error("用户ID格式错误"));
            return;
        }

        try {
            List<Cart> cartList = cartService.findCartListByUserId(userId);
            JSONObject result = new JSONObject();
            result.put("cartItemList", cartList);
            writeJson(resp, success(result));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp,error("查询购物车失败"));
        }
    }

    private void updateQuantity(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        Integer id = params.getInteger("id");
        Integer userId = params.getInteger("userId");
        Integer quantity = params.getInteger("quantity");

        if (id == null || userId == null || quantity == null || quantity < 1) {
            writeJson(resp, error("参数错误"));
            return;
        }

        try {
            boolean ok = cartService.updateCartQuantityById(id, userId, quantity);
            writeJson(resp, ok ? success("更新成功") : error("未找到购物车记录"));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, error("更新失败"));
        }
    }

    private void deleteItem(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        Integer id = params.getInteger("id");
        Integer userId = params.getInteger("userId");
        if (id == null || userId == null) {
            writeJson(resp, error("参数错误"));
            return;
        }

        try {
            boolean ok = cartService.deleteCartItem(id, userId);
            writeJson(resp, ok ? success("删除成功") : error("未找到购物车记录"));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, error("删除失败"));
        }
    }

    private void batchDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        List<Integer> ids = params.getList("ids", Integer.class);
        Integer userId = params.getInteger("userId");
        if (ids == null || ids.isEmpty() || userId == null) {
            writeJson(resp, error("参数错误"));
            return;
        }

        try {
            int count = cartService.batchDeleteCartItems(ids, userId);
            writeJson(resp, success(count));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, error("批量删除失败"));
        }
    }

    private void clear(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JSONObject params = JSONObject.parseObject(req.getReader().readLine());
        Integer userId = params.getInteger("userId");
        if (userId == null) {
            writeJson(resp, error("参数错误"));
            return;
        }

        try {
            int count = cartService.clearCart(userId);
            writeJson(resp, success(count));
        } catch (Exception e) {
            e.printStackTrace();
            writeJson(resp, error("清空失败"));
        }
    }
}
