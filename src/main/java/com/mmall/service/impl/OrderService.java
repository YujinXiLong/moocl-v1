package com.mmall.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.OrderItemMapper;
import com.mmall.dao.OrderMapper;
import com.mmall.dao.PayInfoMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Order;
import com.mmall.pojo.OrderItem;
import com.mmall.pojo.PayInfo;
import com.mmall.pojo.Product;
import com.mmall.pojo.Shipping;
import com.mmall.service.IOrderService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.FTPUtil_1;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RandomUtil;
import com.mmall.vo.OrderItemVO;
import com.mmall.vo.OrderProductVO;
import com.mmall.vo.OrderVO;
import com.mmall.vo.ShippingVO;
@Transactional
@Service("iOrderService")
public class OrderService implements IOrderService {
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderItemMapper orderItemMapper;
	@Autowired
	private PayInfoMapper payInfoMapper;
	@Autowired
	private CartMapper cartMapper;
	@Autowired
	private ProductMapper productMapper;
	@Autowired
	private ShippingMapper shippingMapper;
	
	
	@Value("${FTP_HOST}")
	String host;
	@Value("${FTP_PORT}")
	Integer port;
	@Value("${FTP_USERNAME}")
	String username;
	@Value("${FTP_PASSWORD}")
	String password;
	@Value("${FTP_BASEPATH}")
	String basePath;
	@Value("${FTP_IMAGEURL}")
	String imageURL;
	
	private static AlipayTradeService   tradeService;
	private static Log                  log = LogFactory.getLog(OrderService.class);
	
	
	public ServerResponse<?> createOrder(Integer userId,Integer shippingId){
		if (shippingId == null) {
			return ServerResponse.creatByError("给个收货地址");
		}
		List<Cart> carts = cartMapper.selectCheckedCartByUserId(userId);
		if (carts.isEmpty()) {
			return ServerResponse.creatByError("您的购物车还没有商品，无法下单");
		}
		
		Order order = new Order();
		
		Long orderNo = RandomUtil.getNO();
		//选择购物中已勾选的
		ServerResponse<?> response = this.getOrderItemList(carts, orderNo);
		if (!response.isSuccess()) {
			return response;
		}
		
		List<OrderItem> orderItems = (List<OrderItem>) response.getData();
		
		//向数据库插入OrderItem
		int count = orderItems.size();
		int countResult = orderItemMapper.insertAll(orderItems);
		if (countResult!=count) {
			return ServerResponse.creatByError("订单子订单生成异常");
		}
		
		//减少各产品库存
		if (!this.reduceProductStock(orderItems)) {
			return ServerResponse.creatByError("库存不足");
		}
		
		//清空购物车
		this.cleanCart(carts);
		
		//得到订单总价
		BigDecimal payment = this.getOrderTotalPrice(orderItems);
		
		//向数据库插入order
		order = this.OrderToDateSource(userId, shippingId, payment, orderNo);
		
		if (order == null) {
			return ServerResponse.creatBySuccessMsg("订单生成异常");
		}
		//开始组装
		
		
		return ServerResponse.creatBySuccess(this.tranceToOrderVO(order, orderItems));
	}
	
	
	
	
	//从购物车得到OrderItem集合，用来做插入的,其中要检查商品状态，商品库存
	private ServerResponse<?> getOrderItemList(List<Cart> carts,Long orderNo){
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		for (Cart cart : carts) {
			
			Product product = productMapper.selectByPrimaryKey(cart.getProductId());
			if (product == null) {
				return ServerResponse.creatByError("产品删除或者下架");
			}
			//防止 null == 0
			if (product.getStatus().intValue() != Const.ProductStatusEnum.ON_SALE.getCode()) {
				return ServerResponse.creatByError("产品删除或者下架");
			}
			//单线程下，其实是不可能超过的
			if (cart.getQuantity()>product.getStock()) {
				return ServerResponse.creatByError("现在库存不足");
			}
			
			
			OrderItem orderItem = new OrderItem();
			orderItem.setUserId(cart.getUserId());
			orderItem.setOrderNo(orderNo);
			orderItem.setProductId(cart.getProductId());
			orderItem.setProductName(product.getName());
			orderItem.setCurrentUnitPrice(product.getPrice());
			orderItem.setProductImage(product.getMainImage());
			orderItem.setQuantity(cart.getQuantity());
			orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity().doubleValue()));
			orderItems.add(orderItem);
		}
		return ServerResponse.creatBySuccess(orderItems);
	}
	
	//减少各产品库存
	private boolean reduceProductStock(List<OrderItem> orderItems) {
		for (OrderItem orderItem : orderItems) {
			Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
			int count = product.getStock()-orderItem.getQuantity();
			if (count<0) {
				return false;
			}
			product.setStock(product.getStock()-orderItem.getQuantity());
			productMapper.updateByPrimaryKeySelective(product);
		}
		return true;
	}
	
	//清空购物车
	private void cleanCart(List<Cart> carts) {
		for (Cart cart : carts) {
			cartMapper.deleteByPrimaryKey(cart.getId());
		}
	}
	
	//得到订单总价
	private BigDecimal getOrderTotalPrice(List<OrderItem> orderItems) {
		BigDecimal payment = new BigDecimal(0);
		
		for (OrderItem orderItem : orderItems) {
			payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
		}
		return payment;
	}
	
	//生成订单，向数据库插入订单
	private Order OrderToDateSource(Integer userId,Integer shippingId,BigDecimal payment,Long orderNo) {
		Order order = new Order();
		order.setUserId(userId);
		order.setOrderNo(orderNo);
		order.setPayment(payment);
		order.setPaymentType(Const.PaymentType.ONLINE_PAY.getCode());
		order.setStatus(Const.OrderStatus.NOPAY.getCode());
		order.setPostage(0);
		order.setShippingId(shippingId);
		
		
		//各个时间没有设置上
		int count = orderMapper.insert(order);
		if (count == 0 ) {
			return null;
		}
		return order;
	}
	
	//等到支付时，更新支付平台
	private OrderVO tranceToOrderVO(Order order,List<OrderItem> orderItems) {
		OrderVO orderVO = new OrderVO();
		orderVO.setOrderNo(order.getOrderNo());
		orderVO.setPayment(order.getPayment());
		orderVO.setPaymentType(order.getPaymentType());
		orderVO.setPaymentTypeDesc(Const.getPaymentTypeDesc(order.getPaymentType()));
		orderVO.setStatus(order.getStatus());
		orderVO.setStatusDesc(Const.getOrderStatusDesc(order.getStatus()));
		orderVO.setPostage(order.getPostage());
		//时间处理
		Order orderInDateResouce = orderMapper.selectByPrimaryKey(order.getId());
		orderVO.setCreateTime(DateTimeUtil.DateToStr(orderInDateResouce.getCreateTime()));
		orderVO.setPaymentTime(DateTimeUtil.DateToStr(orderInDateResouce.getPaymentTime()));
		orderVO.setSendTime(DateTimeUtil.DateToStr(orderInDateResouce.getSendTime()));
		orderVO.setEndTime(DateTimeUtil.DateToStr(orderInDateResouce.getEndTime()));
		orderVO.setCloseTime(DateTimeUtil.DateToStr(orderInDateResouce.getCloseTime()));
		orderVO.setUpdateTime(DateTimeUtil.DateToStr(orderInDateResouce.getUpdateTime()));
		orderVO.setShippingId(order.getShippingId());
		Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
		if (shipping != null) {
			orderVO.setReceiverName(shipping.getReceiverName());
			orderVO.setShippingVO(this.tranceToShippingVO(shipping));
		}
		
		orderVO.setImageURI(PropertiesUtil.getProperty("FTP_IMAGEURL"));
		
		List<OrderItemVO> orderItemVOList = new ArrayList<OrderItemVO>();
		for (OrderItem orderItem : orderItems) {
			orderItemVOList.add(this.tranceToOrderItemVO(orderItem));
		}
		orderVO.setOrderItemVOList(orderItemVOList);
		
		return orderVO;
	}
	
	private ShippingVO tranceToShippingVO(Shipping shipping) {
		ShippingVO shippingVO = new ShippingVO();
		shippingVO.setReceiverAddress(shipping.getReceiverAddress());
		shippingVO.setReceiverCity(shipping.getReceiverCity());
		shippingVO.setReceiverDistrict(shipping.getReceiverDistrict());
		shippingVO.setReceiverMobile(shipping.getReceiverMobile());
		shippingVO.setReceiverName(shipping.getReceiverName());
		shippingVO.setReceiverPhone(shipping.getReceiverPhone());
		shippingVO.setReceiverProvince(shipping.getReceiverProvince());
		shippingVO.setReceiverZip(shipping.getReceiverZip());
		return shippingVO;
	}
	
	private OrderItemVO tranceToOrderItemVO(OrderItem orderItem) {
		OrderItemVO orderItemVO = new OrderItemVO();
		orderItemVO.setOrderNo(orderItem.getOrderNo());
		orderItemVO.setProductId(orderItem.getProductId());
		orderItemVO.setProductImage(orderItem.getProductImage());
		orderItemVO.setProductName(orderItem.getProductName());
		orderItemVO.setQuantity(orderItem.getQuantity());
		orderItemVO.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
		orderItemVO.setTotalPrice(orderItem.getTotalPrice());
		return orderItemVO;
	}
	
	//==================================================================
	
	//展示订单的商品信息
	public ServerResponse<?> playOrderProduct(Long orderNo,Integer userId){
		List<OrderItem> orderItems = orderItemMapper.selectByOrderNoAndUserId(orderNo, userId);
		if (orderItems.isEmpty()) {
			return ServerResponse.creatByError("该订单无商品详情");
		}
		
		List<OrderItemVO> orderItemVOs = new ArrayList<OrderItemVO>();
		for (OrderItem orderItem : orderItems) {
			orderItemVOs.add(this.tranceToOrderItemVO(orderItem));
		}
		OrderProductVO orderProductVO = new OrderProductVO();
		orderProductVO.setOrderItemVOList(orderItemVOs);
		orderProductVO.setProductTotalPrice(this.getOrderTotalPrice(orderItems));
		orderProductVO.setImageURI(PropertiesUtil.getProperty("FTP_IMAGEURL"));
		
		return ServerResponse.creatBySuccess(orderProductVO);
	}
	
	
	//展示个人所有订单
	public ServerResponse<?> playOrderList(Integer userId,Integer pageNum,Integer pageSize){
		PageHelper.startPage(pageNum, pageSize);
		List<Order> orders = orderMapper.selectByUserId(userId);
		if (orders.isEmpty()) {
			return ServerResponse.creatByError("您还没有订单");
		}
		List<OrderVO> orderVOs = new ArrayList<OrderVO>();
		for ( Order	order : orders) {
			List<OrderItem> orderItems = orderItemMapper.selectByOrderNoAndUserId(order.getOrderNo(), userId);
			orderVOs.add(this.tranceToOrderVO(order,orderItems));
		}
		PageInfo pageInfo = new PageInfo(orders);
		pageInfo.setList(orderVOs);
		return ServerResponse.creatBySuccess(pageInfo);
	}
	
	//展示订单详情
	public ServerResponse<?> playOrderDetail(Integer userId,Long orderNo) {
			Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
			if (order==null) {
				return ServerResponse.creatByError("该订单不存在");
			}
			List<OrderItem> orderItems = orderItemMapper.selectByOrderNoAndUserId(order.getOrderNo(), userId);
			
			return ServerResponse.creatBySuccess(this.tranceToOrderVO(order, orderItems));
	}
	
	//取消订单
	public ServerResponse<?> cancelOrder(Integer userId,Long orderNo){
		Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
		if (order==null) {
			return ServerResponse.creatByError("该订单不存在,无法取消");
		}
		if (order.getStatus().intValue() >= Const.OrderStatus.PAID.getCode()) {
			return ServerResponse.creatByError("订单已经付款，无法取消");
		}
		order.setStatus(Const.OrderStatus.CANCELED.getCode());
		int count = orderMapper.updateByPrimaryKeySelective(order);
		if (count == 0) {
			return ServerResponse.creatByError("取消失败");
		}
		return ServerResponse.creatBySuccessMsg("取消成功");
	}
	
	
	
	//====================================================================
	
	
	
	public ServerResponse<?> manageOrderList(Integer pageNum,Integer pageSize){
		PageHelper.startPage(pageNum, pageSize);
		List<Order> orders = orderMapper.selectAll();
		if (orders.isEmpty()) {
			return ServerResponse.creatByError("没有该订单");
		}
		List<OrderVO> orderVOs = new ArrayList<OrderVO>();
		for ( Order	order : orders) {
			List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(order.getOrderNo());
			orderVOs.add(this.tranceToOrderVO(order,orderItems));
		}
		PageInfo pageInfo = new PageInfo(orders);
		pageInfo.setList(orderVOs);
		return ServerResponse.creatBySuccess(pageInfo);
	}
	
	//以后是多功能的
	public ServerResponse<?> manageSearch(Long orderNo,Integer pageNum,Integer pageSize){
		PageHelper.startPage(pageNum, pageSize);
		Order temorder = orderMapper.selectByOrderNo(orderNo);
		List<Order> orders = new ArrayList<Order>();
		orders.add(temorder);
		if (orders.isEmpty()) {
			return ServerResponse.creatByError("没有该订单");
		}
		List<OrderVO> orderVOs = new ArrayList<OrderVO>();
		for ( Order	order : orders) {
			List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(order.getOrderNo());
			orderVOs.add(this.tranceToOrderVO(order,orderItems));
		}
		PageInfo pageInfo = new PageInfo(orders);
		pageInfo.setList(orderVOs);
		return ServerResponse.creatBySuccess(pageInfo);
	}
	
	
	public ServerResponse<?> manageDetail(Long orderNo){
		Order order = orderMapper.selectByOrderNo(orderNo);
		if (order==null) {
			return ServerResponse.creatByError("该订单不存在");
		}
		List<OrderItem> orderItems = orderItemMapper.selectByOrderNo(orderNo);
		
		return ServerResponse.creatBySuccess(this.tranceToOrderVO(order, orderItems));
	}
	
	
	public ServerResponse<?> manageSendGoods(Long orderNo){
		Order order = orderMapper.selectByOrderNo(orderNo);
		if (order==null) {
			return ServerResponse.creatByError("该订单不存在");
		}
		if (order.getStatus().intValue()==Const.OrderStatus.PAID.getCode()) {
			order.setStatus(Const.OrderStatus.SHIPPED.getCode());
			order.setSendTime(new Date());
			orderMapper.updateByPrimaryKeySelective(order);
			return ServerResponse.creatBySuccessMsg("发货成功");
		}
		return ServerResponse.creatByError("该订单未付款");
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
    
	public ServerResponse<?> pay(Long orderNo,Integer userId,String path){

		Order order = orderMapper.selectByOrderNoAndUserId(orderNo,userId);
		if (order == null) {
			return ServerResponse.creatByError("该订单不存在");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", orderNo);
		List<OrderItem> orderItems = orderItemMapper.selectByOrderNoAndUserId(orderNo,userId);
		Configs.init("resource/zfbinfo.properties");
		 
		tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
		 
		// (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        //String outTradeNo = "tradeprecreate" + System.currentTimeMillis()
        //                   + (long) (Math.random() * 10000000L);
		String outTradeNo = order.getOrderNo().toString();
		
        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "xxx品牌xxx门店当面付扫码消费";
        
        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        //String totalAmount = "0.01";
          String totalAmount = order.getPayment().toString();
          
        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        //String body = "购买商品3件共20.00元";
          String body = new StringBuffer().append("购买商品")
        		  .append(String.valueOf(orderItems.size()))
        		  .append("件共")
        		  .append(String.valueOf(order.getPayment())).append("元").toString();
          
        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
       // GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
       // goodsDetailList.add(goods1);

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
        //GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
       // goodsDetailList.add(goods2);
        
        
        for (OrderItem orderItem : orderItems) {
        	GoodsDetail goodsDetail = GoodsDetail.newInstance(orderItem.getProductId().toString(),
											        			orderItem.getProductName(),
											        			Long.valueOf(BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(), 100d).toString()),
											        			orderItem.getQuantity());
        	goodsDetailList.add(goodsDetail);
		}
        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
            .setSubject(subject)
            .setTotalAmount(totalAmount)
            .setOutTradeNo(outTradeNo)
            .setUndiscountableAmount(undiscountableAmount)
            .setSellerId(sellerId)
            .setBody(body)
            .setOperatorId(operatorId)
            .setStoreId(storeId)
            .setExtendParams(extendParams)
            .setTimeoutExpress(timeoutExpress)
            .setNotifyUrl(PropertiesUtil.getProperty("alipay_callback"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
            .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");
                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);
                //需要修改为运行机器上的路径
                //String filePath = String.format("/Users/sudo/Desktop/qr-%s.png",response.getOutTradeNo());
                //log.info("filePath:" + filePath);
                //ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                
                File pathFloder = new File(path);
                if (!pathFloder.exists()) {
					pathFloder.setWritable(true);
					pathFloder.mkdirs();
				}
                //上传二维码
                String filePath = String.format(path+"/qr-%s.png",response.getOutTradeNo());
                log.info("filePath:" + filePath);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
               
                File targetFile = new File(filePath);
                String filename = String.format("qr-%s.png",response.getOutTradeNo());
                String towCodeFloder = "/two-dimensional-code/";
				try {
					FTPUtil_1.upload(host, port, username, password, basePath, towCodeFloder , filename, new FileInputStream(targetFile));
				} catch (FileNotFoundException e) {
					log.info("二维码上传异常",e);
				}
				targetFile.delete();
	            map.put("qrPath", imageURL+towCodeFloder+filename);
	            return ServerResponse.creatBySuccess(map);
            
            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServerResponse.creatBySuccess("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServerResponse.creatBySuccess("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.creatBySuccess("不支持的交易状态，交易返回异常!!!");
        }
	}
	

    public Object alipayCallback(Map<String, String> params){
    	String outTradeNo = params.get("out_trade_no");
    	String tradeStatus = params.get("trade_status");
    	String tradeNo = params.get("trade_no");
    	//outTradeNo只能是数字的字符串，不然格式化异常
    	Order order = orderMapper.selectByOrderNo(Long.valueOf(outTradeNo));
    	if (order == null) {
			return ServerResponse.creatByError("该订单不存在，用户没有该订单");
		}
    	//用系统订单状态判断                                    此时是否重复调用
    	if (order.getStatus()>=Const.OrderStatus.PAID.getCode()) {
			return ServerResponse.creatBySuccessMsg("支付宝重复调用");
		}
    	//订单没付钱，支付宝收到钱了
    	if (tradeStatus.equals(Const.AlipayCallbackStatus.TRADE_STATUS_TRADE_SUCCESS)) {
    		//更新支付宝收钱的时间----,new Date()数据库可以接受吗？ 还是mybatis做了处理
    		order.setStatus(Const.OrderStatus.PAID.getCode());
    		order.setPaymentTime(DateTimeUtil.StrToDate(params.get("gmt_payment")));
        	orderMapper.updateByPrimaryKeySelective(order);
		}
    	
    	//订单没付钱，支付宝收到钱了， 或者支付宝等待买家付款，或者订单取消了
    	PayInfo payInfo = new PayInfo();
    	payInfo.setOrderNo(order.getOrderNo());
    	payInfo.setUserId(order.getUserId());
    	payInfo.setPayPlatform(Const.PayPlatform.ALIPAY.getCode());
    	payInfo.setPlatformNumber(tradeNo);
    	payInfo.setPlatformStatus(tradeStatus);
    	//异常
    	payInfoMapper.insert(payInfo);
    	return ServerResponse.creatBySuccess();
    }
    
    
   public ServerResponse<?> queryOrderPayStatus(Integer userId,Long orderNo) {
	   Order order = orderMapper.selectByOrderNoAndUserId(orderNo, userId);
	   if (order == null) {
		   return ServerResponse.creatByError("该用户没有该订单");
	   }
	   if (order.getStatus()>=Const.OrderStatus.PAID.getCode()) {
		   return ServerResponse.creatBySuccess();
	   }
	   return ServerResponse.creatByError();
   } 
   
   
// 简单打印应答
   private void dumpResponse(AlipayResponse response) {
       if (response != null) {
           log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
           if (StringUtils.isNotEmpty(response.getSubCode())) {
               log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                   response.getSubMsg()));
           }
           log.info("body:" + response.getBody());
       }
   }
}
