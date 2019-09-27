package com.mmall.controller.backend;

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
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;

@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {
	
	@Autowired
	private IOrderService iOrderService;
	
	@RequestMapping(value = "list.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> list(HttpSession session,
			@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
			@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		if (user.getRole().intValue()==Const.Role.Role_ADMIN) {
			
			return iOrderService.manageOrderList(pageNum, pageSize);
		}
		
		return ServerResponse.creatByError("您的权限不够");
	}
	
	//以后可以做扩展模糊查询，等，现在就只能按订单号查询
	@RequestMapping(value = "search.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> search(HttpSession session,
			@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
			@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum,
			Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		if (user.getRole().intValue()==Const.Role.Role_ADMIN) {
			
			return iOrderService.manageSearch(orderNo, pageNum, pageSize);
		}
		
		return ServerResponse.creatByError("您的权限不够");
	}
	
	@RequestMapping(value = "detail.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> detail(HttpSession session,Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		if (user.getRole().intValue()==Const.Role.Role_ADMIN) {
			
			return iOrderService.manageDetail(orderNo);
		}
		
		return ServerResponse.creatByError("您的权限不够");
	}
	
	@RequestMapping(value = "send_goods.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> sendGoods(HttpSession session,Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		if (user.getRole().intValue()==Const.Role.Role_ADMIN) {
			
			return iOrderService.manageSendGoods(orderNo);
		}
		
		return ServerResponse.creatByError("您的权限不够");
	}

}
