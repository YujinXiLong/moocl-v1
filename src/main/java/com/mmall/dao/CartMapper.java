package com.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.Cart;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);
    
    
    
    
    Cart selectByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);
    
    List<Cart> selectByUserId(Integer userId);
    
    int AllCheckedStatus(Integer userId);
    
    int deleteByUserIdAndProductIds(@Param("userId") Integer userId, @Param("productIdList") List<Integer> productIdList);
    
    int updateByUserIdAndProductId(Cart record);
    
    int checkedOrUnchecked(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);
    
    int selectCartProductCount(Integer userId);
    
    
    List<Cart> selectCheckedCartByUserId(Integer userId);
    
}