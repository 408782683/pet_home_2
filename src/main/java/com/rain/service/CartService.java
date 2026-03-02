package com.rain.service;
import com.rain.entity.Cart;
import com.rain.mapper.CartMapper;
import java.sql.SQLException;
import java.util.List;
public class CartService {
    private CartMapper cartMapper = new CartMapper();

    //添加购物车
    public void addToCart(Integer productId,Integer userId,Integer quantity) throws SQLException {
        boolean flag = cartMapper.idAddCart(productId, userId);
        if(flag){
            //说明购物车中已存在
            cartMapper.updateCartQuantity(productId, userId, quantity);
        }else{
            //说明不存在购物车中
            cartMapper.addToCart(productId, userId, quantity);
        }
    }



    public List<Cart> findCartListByUserId(Integer userId) throws SQLException {
        return cartMapper.findCartListByUserId(userId);
    }

    public boolean updateCartQuantityById(Integer id, Integer userId, Integer quantity) throws SQLException {
        return cartMapper.updateCartQuantityById(id, userId, quantity) > 0;
    }

    public boolean deleteCartItem(Integer id, Integer userId) throws SQLException {
        return cartMapper.deleteByIdAndUserId(id, userId) > 0;
    }

    public int batchDeleteCartItems(List<Integer> ids, Integer userId) throws SQLException {
        int count = 0;
        for (Integer id : ids) {
            count += cartMapper.deleteByIdAndUserId(id, userId);
        }
        return count;
    }

    public int clearCart(Integer userId) throws SQLException {
        return cartMapper.clearByUserId(userId);
    }
}
