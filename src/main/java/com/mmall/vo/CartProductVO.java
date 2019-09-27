package com.mmall.vo;

import java.math.BigDecimal;

public class CartProductVO {
	private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;

    private String productName;
    private String productSubtitle;
    private String productMainImage;
    private Integer productStatus;
    private Integer productStock;
    private BigDecimal price;
    
   
	private Integer productChecked;
    private String limitQuantity;
    private BigDecimal productTotalPrice;
    
  

	public CartProductVO() {
	}
    
	public CartProductVO(Integer id, Integer userId, Integer productId, Integer quantity, String productName,
			String productSubtitle, String productMainImage, Integer productStatus, Integer productStock,
			Integer productChecked, String limitQuantity, BigDecimal productTotalPrice,BigDecimal price) {
		super();
		this.id = id;
		this.userId = userId;
		this.productId = productId;
		this.quantity = quantity;
		this.productName = productName;
		this.productSubtitle = productSubtitle;
		this.productMainImage = productMainImage;
		this.productStatus = productStatus;
		this.productStock = productStock;
		this.productChecked = productChecked;
		this.limitQuantity = limitQuantity;
		this.productTotalPrice = productTotalPrice;
		this.price = price;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductSubtitle() {
		return productSubtitle;
	}
	public void setProductSubtitle(String productSubtitle) {
		this.productSubtitle = productSubtitle;
	}
	public String getProductMainImage() {
		return productMainImage;
	}
	public void setProductMainImage(String productMainImage) {
		this.productMainImage = productMainImage;
	}
	public Integer getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;
	}
	public Integer getProductStock() {
		return productStock;
	}
	public void setProductStock(Integer productStock) {
		this.productStock = productStock;
	}
	public BigDecimal getPrice() {
			return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public void setProductChecked(Integer productChecked) {
		this.productChecked = productChecked;
	}
	public void setLimitQuantity(String limitQuantity) {
		this.limitQuantity = limitQuantity;
	}
	public BigDecimal getProductTotalPrice() {
		return productTotalPrice;
	}
	public void setProductTotalPrice(BigDecimal productTotalPrice) {
		this.productTotalPrice = productTotalPrice;
	}
	 public Integer getProductChecked() {
			return productChecked;
	}

	public String getLimitQuantity() {
			return limitQuantity;
	}
	
}
