package com.mmall.controller.potal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;

@Controller
@RequestMapping("/order/")
public class OrderController {
	@Autowired
	private IOrderService iOrderService;
	
	private Log log = LogFactory.getLog(OrderController.class);
	
	@RequestMapping(value = "pay.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> pay(Long orderNo,HttpSession session,HttpServletRequest request){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		String path = request.getServletContext().getRealPath("upload");
		return iOrderService.pay(orderNo, user.getId(), path);
		
	}
	
	@RequestMapping(value = "alipay_callback.do",method = RequestMethod.POST)
	@ResponseBody
	public Object alipayCallback(HttpServletRequest request) {
		Map<String, String[]> aliParams = request.getParameterMap();
		Map<String,String> params = new HashMap<String, String>();
		for (Iterator<String> iterator = request.getParameterMap().keySet().iterator() ; iterator.hasNext();) {
			String key = (String) iterator.next();
			String []value = aliParams.get(key);
			StringBuilder content = new StringBuilder("");
			for (int i = 0; i < value.length; i++) {
				content = i+1-value.length == 0 ? content.append(value[0]) : content.append(value[0]).append(",");
			}
			params.put(key, content.toString());
		}
		//调用对象验证
		params.remove("sign_type");
		try {
			boolean checkResult = AlipaySignature.rsaCheckV2(params,Configs.getAlipayPublicKey(),"utf-8", Configs.getSignType());
			if (checkResult == false) {
				return ServerResponse.creatByError("你不是支付宝，别再调用我的接口了，不然我找网警了");
			}
		} catch (AlipayApiException e) {
			log.info("支付宝回掉异常",e);
			e.printStackTrace();
		}
		
		//验证数据
		
		
		
		//
		ServerResponse<?> response = (ServerResponse<?>) iOrderService.alipayCallback(params);
		if (response.isSuccess()) {
			return Const.AlipayCallbackStatus.RESPONSE_SUCCESS;
		}
		return Const.AlipayCallbackStatus.RESPONSE_FAILED;
	}
	
	//查询支付成功没有
	@RequestMapping(value = "query_order_pay_status.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<Boolean> queryOrderPayStatus(HttpServletRequest request,Long orderNo,HttpSession session) {
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
		}
		ServerResponse<?> response = iOrderService.queryOrderPayStatus(user.getId(), orderNo);
				if (response.isSuccess()) {
					return ServerResponse.creatBySuccess(true);
				}
		
		return ServerResponse.creatBySuccess(false);
	}
	
	
	
	
	@RequestMapping(value = "create.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> create(HttpSession session,Integer shippingId){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		return iOrderService.createOrder(user.getId(), shippingId);
	}
	
	@RequestMapping(value = "get_order_cart_product.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> getOrderCartProduct(HttpSession session,Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		
		return iOrderService.playOrderProduct(orderNo, user.getId());
	}
	
	@RequestMapping(value = "list.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> list(HttpSession session,
			@RequestParam(value = "pageSize",defaultValue = "10")Integer pageSize,
			@RequestParam(value = "pageNum",defaultValue = "1")Integer pageNum){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		return iOrderService.playOrderList(user.getId(), pageNum, pageSize);
	}
	
	@RequestMapping(value = "detail.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> detail(HttpSession session,Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		
		return iOrderService.playOrderDetail(user.getId(), orderNo);
	}
	
	@RequestMapping(value = "cancel.do",method = RequestMethod.POST)
	@ResponseBody
	public ServerResponse<?> cancel(HttpSession session,Long orderNo){
		User user = (User) session.getAttribute(Const.CURRENT_USER);
		if (user == null) {
			return ServerResponse.creatByError(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
		}
		
		
		return iOrderService.cancelOrder(user.getId(), orderNo);
	}
}
