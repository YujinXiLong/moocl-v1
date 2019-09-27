package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVO {
	private List<CartProductVO> cartProductVoList;
	private boolean allChecked;
	private BigDecimal cartTotalPrice;
	private String imageURI;
	
	
	public CartVO() {
		// TODO Auto-generated constructor stub
	}


	public CartVO(List<CartProductVO> cartProductVoList, boolean allChecked, BigDecimal cartTotalPrice,
			String imageURI) {
		super();
		this.cartProductVoList = cartProductVoList;
		this.allChecked = allChecked;
		this.cartTotalPrice = cartTotalPrice;
		this.imageURI = imageURI;
	}


	public List<CartProductVO> getCartProductVoList() {
		return cartProductVoList;
	}


	public void setCartProductVoList(List<CartProductVO> cartProductVoList) {
		this.cartProductVoList = cartProductVoList;
	}


	public boolean isAllChecked() {
		return allChecked;
	}


	public void setAllChecked(boolean allChecked) {
		this.allChecked = allChecked;
	}


	public BigDecimal getCartTotalPrice() {
		return cartTotalPrice;
	}


	public void setCartTotalPrice(BigDecimal cartTotalPrice) {
		this.cartTotalPrice = cartTotalPrice;
	}


	public String getImageURI() {
		return imageURI;
	}


	public void setImageURI(String imageURI) {
		this.imageURI = imageURI;
	}
	
	
}