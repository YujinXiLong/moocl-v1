package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;

public interface IShippingService {
	public ServerResponse<?> addShipping(Integer userId, Shipping shipping);
	
	public ServerResponse<?> deleteShipping(Integer userId, Integer shippingId);
	
	public ServerResponse<?> updateShipping(Integer userId, Shipping shipping);
	
	public ServerResponse<?> selectShipping(Integer userId, Integer shippingId);
	
	public ServerResponse<?> selectAll(Integer userId, Integer pageNum, Integer pageSize);
	
}
