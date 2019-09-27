package com.mmall.controller.potal;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;

@Controller()
@RequestMapping("/shipping/")
public class ShippingController {
	@Autowired
	private IShippingService iShippingService;
	
	
	//不给shipping传递值，或者参数名字不为shipping，那么shipping并不为null,而是shipping = new shipping();
	@RequestMapping(value = "add.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> add(Shipping shipping,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		if (shipping.getReceiverName() == null) {
			shipping = null;
		}
		return iShippingService.addShipping(user.getId(),shipping);
	}
	
	@RequestMapping(value = "del.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> del(Integer shippingId,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		return iShippingService.deleteShipping(user.getId(), shippingId);
	}
	
	@RequestMapping(value = "update.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> update(Shipping shipping,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		System.out.println(shipping.getReceiverName());
		if (shipping.getReceiverName() == null) {
			shipping = null;
		}
		return iShippingService.updateShipping(user.getId(), shipping);
	}
	
	@RequestMapping(value = "select.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> select(Integer shippingId,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		return iShippingService.selectShipping(user.getId(), shippingId);
	}
	
	@RequestMapping(value = "list.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> list(@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		return iShippingService.selectAll(user.getId(), pageNum, pageSize);
	}
	
	
	
}
