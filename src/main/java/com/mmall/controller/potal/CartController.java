package com.mmall.controller.potal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;

@Controller
@RequestMapping("/cart/")
public class CartController {
	@Autowired
	private ICartService iCartService;
	
	@RequestMapping(value = "list.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> list(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		
		return iCartService.getCartProduct(user.getId());
	}
		
	@RequestMapping(value = "add.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> add(Integer count,Integer productId,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		
		return iCartService.add(user.getId(), count, productId);
	}

	@RequestMapping(value = "delete_product.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> delete(String productIds,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		return iCartService.delete(user.getId(), productIds);
	}
	
	@RequestMapping(value = "update.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> update(Integer count,Integer productId,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		
		return iCartService.updateQuantity(user.getId(), productId, count);
	}
	
	
	
	@RequestMapping(value = "select.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> select(Integer productId,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
	}
	
	@RequestMapping(value = "un_select.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> unSelect(Integer productId,HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		
		return iCartService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
	}
	
	@RequestMapping(value = "select_all.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> selectAll(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		
		return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.CHECKED);
	}
	
	@RequestMapping(value = "un_select_all.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> unSelectAll(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		
		return iCartService.selectOrUnSelect(user.getId(), null, Const.Cart.UN_CHECKED);
	}
	
	
	
	@RequestMapping(value = "get_cart_product_count.do",method = RequestMethod.GET)
	@ResponseBody
	public ServerResponse<?> getCartProductCount(HttpSession session){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),"请登录");
		}
		
		return iCartService.getCartProductCount(user.getId());
	}
	
	
	
}
