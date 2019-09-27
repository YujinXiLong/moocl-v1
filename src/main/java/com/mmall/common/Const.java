package com.mmall.common;

import java.util.Set;

import com.google.common.collect.Sets;

public class Const {
		public static final String CURRENT_USER = "currentUser";
		public static final String USERNAME = "username";
		public static final String EMAIL = "email";
		
		public interface ProductListOrderBy{
			Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc","price_desc");//看源码
		}
		
		public interface Role{
			int Role_CUSTEMER = 0;
			int Role_ADMIN = 1; //相当于静态不变量
		}
		
		public interface Cart{
			Integer CHECKED = 1;
			Integer UN_CHECKED = 0;
			String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
			String LIMIT_NUM_SUCCES = "LIMIT_NUM_SUCCES";
		}
		
		
		//编译后的enum为final修饰的类，这种类有什么特点
		public enum ProductStatusEnum{
			
			ON_SALE(1,"在售"),
			OBTAINED(2,"下架"),
			DELETED(3,"删除");
			//这里的字段可以不加final修饰，那么他就是变量，加了final修饰，他就是常量，
			//但是我们需要一个类，这种类，在这个类型（构造方法被私有话，所以这个类型的类数量一定）里面是唯一的，如果字段不能被改变就更好了
			//还有一个问题，code不能用static修饰；为什？----ON_SALE(1,"在售")==相当于，首先初始话，如Person person ;这时候，会赋值为null;如果是Person person = new Person();那么这时的pserson 是不为null的对象，
																	//最后，还可以通过，构造方法初始话，这里给code加上static的话，在初始化ON_SALE(1,"在售")时，会调用构造方法，但是，按照static的级别，此时的code字段并未初始化
																	//所以会报错，其实可以把code放在最前面，即使用static修饰也没关系，但是enum类不允许，放在最前面，普通类可以。
			private final int code;
			private final String desc;
			//改成private,调用不了，为什么？
			public int getCode() {
				return code;
			}
			public String getDesc() {
				return desc;
			}
			private ProductStatusEnum (int code,String desc) {
				this.code = code;
				this.desc = desc;
			}
		}
		
		public enum OrderStatus{
			CANCELED(0,"已取消"),
			NOPAY(10,"未付款"),
			PAID(20,"已付款"),
			SHIPPED(40,"已发货"),
			ORDER_SUCCESS(50,"订单完成"),
			ORDER_CLOSE(60,"订单关闭");
			
			
			public int getCode() {
				return code;
			}

			public String getDesc() {
				return desc;
			}
			
			private  final int code;
			private  final String desc;
			
			private OrderStatus(int code,String desc) {
				this.code = code;
				this.desc = desc;
			}
		}
		
		public interface AlipayCallbackStatus{
			String TRADE_STATUS_TRADE_SUCCESS ="TRADE_SUCCESS";
			String TRADE_STATUS_WAIT_BUYER_PAY ="WAIT_BUYER_PAY";
			
			String RESPONSE_SUCCESS ="success";
			String RESPONSE_FAILED ="failed";
		}
		
		public enum PayPlatform{
			ALIPAY(1,"支付宝");
			private final int code ;
			private final String desc;
			
			private PayPlatform(int code , String desc) {
				this.code = code;
				this.desc = desc;
			}

			public int getCode() {
				return code;
			}

			public String getDesc() {
				return desc;
			}
		}
		
		
		public enum PaymentType{
			ONLINE_PAY(1,"在线支付");
			private final int code ;
			private final String desc;
			
			private PaymentType(int code , String desc) {
				this.code = code;
				this.desc = desc;
			}

			public int getCode() {
				return code;
			}

			public String getDesc() {
				return desc;
			}
			
		}
		
		public static String getPayPlatformDesc(int payPlatformNo) {
			PayPlatform []payPlatforms = PayPlatform.values();
			for (int i = 0; i < payPlatforms.length; i++) {
				if (payPlatforms[i].getCode() == payPlatformNo) {
					return payPlatforms[i].getDesc();
				}
			}
			return "位置的支付平台，请谨慎";
		}
		
		public static String getPaymentTypeDesc(int paymentTypeNo) {
			PaymentType []paymentTypes = PaymentType.values();
			for (int i = 0; i < paymentTypes.length; i++) {
				if (paymentTypes[i].getCode()==paymentTypeNo) {
					return paymentTypes[i].getDesc();
				}
			}
			return "未知的支付方式";
		}
		
		public static String getOrderStatusDesc(int orderStatusNo) {
			OrderStatus []orderStatuss = OrderStatus.values();
			for (int i = 0; i < orderStatuss.length; i++) {
				if (orderStatuss[i].getCode()==orderStatusNo) {
					return orderStatuss[i].getDesc();
				}
			}
			return "未知的订单状态";
		}
		
		
}
