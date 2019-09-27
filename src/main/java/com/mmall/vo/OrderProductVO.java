package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class OrderProductVO {
	private List<OrderItemVO> orderItemVOList;
	
	private String imageURI;
	
	private BigDecimal productTotalPrice;
	
	
	public OrderProductVO() {
	}
	
	public OrderProductVO(List<OrderItemVO> orderItemVOList, String imageURI, BigDecimal productTotalPrice) {
		super();
		this.orderItemVOList = orderItemVOList;
		this.imageURI = imageURI;
		this.productTotalPrice = productTotalPrice;
	}
	
	
	public List<OrderItemVO> getOrderItemVOList() {
		return orderItemVOList;
	}

	public void setOrderItemVOList(List<OrderItemVO> orderItemVOList) {
		this.orderItemVOList = orderItemVOList;
	}

	public String getImageURI() {
		return imageURI;
	}

	public void setImageURI(String imageURI) {
		this.imageURI = imageURI;
	}

	public BigDecimal getProductTotalPrice() {
		return productTotalPrice;
	}

	public void setProductTotalPrice(BigDecimal productTotalPrice) {
		this.productTotalPrice = productTotalPrice;
	}

	

	
}
