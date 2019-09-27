package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVO;

public interface ICartService {
	public ServerResponse<CartVO> getCartProduct(Integer userId);
	
	public ServerResponse<?> add(Integer userId, Integer count, Integer productId);
	
	public ServerResponse<?> delete(Integer userId, String productIds);
	
	public ServerResponse<?> updateQuantity(Integer userId, Integer productId, Integer count);
	
	public ServerResponse<?> selectOrUnSelect(Integer userId, Integer productId, Integer checked);
	
	public ServerResponse<?> getCartProductCount(Integer userId);
}
