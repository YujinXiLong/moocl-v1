package com.mmall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mmall.pojo.Order;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);
    
    
    
    
    Order selectByOrderNoAndUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);
    
    Order selectByOrderNo(Long orderNo);
    
    List<Order> selectByUserId(Integer userId);
    
    List<Order> selectAll();
}