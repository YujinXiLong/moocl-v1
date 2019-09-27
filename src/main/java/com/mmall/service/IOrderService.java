package com.mmall.service;

import java.util.Map;

import com.mmall.common.ServerResponse;

public interface IOrderService {
	public ServerResponse<?> pay(Long orderNo, Integer userId, String path);
	
	public Object alipayCallback(Map<String, String> params);
	
	public ServerResponse<?> queryOrderPayStatus(Integer userId, Long orderNo);
	
	
	
	public ServerResponse<?> createOrder(Integer userId, Integer shippingId);
	
	public ServerResponse<?> playOrderProduct(Long orderNo, Integer userId);
	
	public ServerResponse<?> playOrderList(Integer userId, Integer pageNum, Integer pageSize);
	
	public ServerResponse<?> playOrderDetail(Integer userId, Long orderNo);
	
	public ServerResponse<?> cancelOrder(Integer userId, Long orderNo);
	
	
	
	public ServerResponse<?> manageOrderList(Integer pageNum, Integer pageSize);
	
	public ServerResponse<?> manageSearch(Long orderNo, Integer pageNum, Integer pageSize);
	
	public ServerResponse<?> manageDetail(Long orderNo);
	
	public ServerResponse<?> manageSendGoods(Long orderNo);
}
