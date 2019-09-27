package com.mmall.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
	@Autowired
	private ShippingMapper shippingMapper;

	
	//此时的 shipping不为null
	public ServerResponse<?> addShipping(Integer userId,Shipping shipping){
		if (shipping == null) {
			return ServerResponse.creatByError("参数错误");
		}
		shipping.setUserId(userId);
		int count = shippingMapper.insert(shipping);
		if (count>0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("shippingId", shipping.getId());
			return ServerResponse.creatBySuccess("新建成功", map);
		}
		return ServerResponse.creatByError("新建失败");
	}
	//关联用户和地址
	public ServerResponse<?> deleteShipping(Integer userId,Integer shippingId) {
		int count = shippingMapper.deleteByUserIdAndShipping(userId,shippingId);
		if (count>0) {
			return ServerResponse.creatBySuccessMsg("删除成功");
		}
		return ServerResponse.creatByError("删除失败");
	}
	
	public ServerResponse<?> updateShipping(Integer userId,Shipping shipping) {
		if (shipping == null) {
			return ServerResponse.creatByError("参数错误");
		}
		shipping.setUserId(userId);
		System.out.println(shipping.getReceiverDistrict());
		int count = shippingMapper.updateByUserIdAndShippingId(shipping);
		if (count > 0) {
			return ServerResponse.creatBySuccessMsg("更新成功");
		}
		return ServerResponse.creatByError("更新失败");
	}
	
	public ServerResponse<?> selectShipping(Integer userId,Integer shippingId) {
		Shipping shipping = shippingMapper.selectByUserIdAndShippingId(userId,shippingId);
		if (shipping != null) {
			return ServerResponse.creatBySuccess(shipping);
		}
		return ServerResponse.creatByError("该地址不存在");
	}
	
	public ServerResponse<?> selectAll(Integer userId,Integer pageNum,Integer pageSize){
		PageHelper.startPage(pageNum, pageSize);
		PageInfo<Shipping> pageInfo = new PageInfo<Shipping>(shippingMapper.selectAll(userId));
		return ServerResponse.creatBySuccess(pageInfo);
	}
}
