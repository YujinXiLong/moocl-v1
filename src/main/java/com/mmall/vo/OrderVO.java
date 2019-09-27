package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;


public class OrderVO {
	 	//private Integer id;

	    //private Integer userId;
	
		private Long orderNo;

	    private BigDecimal payment;

	    private Integer paymentType;

	    private Integer postage;

	    private Integer status;
	    
	    private Integer shippingId;

	    
	    

	    private String paymentTime;

	    private String sendTime;

	    private String endTime;

	    private String closeTime;

	    private String createTime;

	    private String updateTime;
	    
	    
	    private String paymentTypeDesc;
	    
	    private String statusDesc;

		private List<OrderItemVO> orderItemVOList;

		private ShippingVO shippingVO;
	    
	    private String receiverName;

		private String imageURI;
	    


		public OrderVO() {}
	    

		


		public OrderVO(Long orderNo, BigDecimal payment, Integer paymentType, Integer postage, Integer status,
				Integer shippingId, String paymentTime, String sendTime, String endTime, String closeTime,
				String createTime, String updateTime, String paymentTypeDesc, String statusDesc,
				List<OrderItemVO> orderItemVOList, ShippingVO shippingVO, String receiverName, String imageURI) {
			super();
			this.orderNo = orderNo;
			this.payment = payment;
			this.paymentType = paymentType;
			this.postage = postage;
			this.status = status;
			this.shippingId = shippingId;
			this.paymentTime = paymentTime;
			this.sendTime = sendTime;
			this.endTime = endTime;
			this.closeTime = closeTime;
			this.createTime = createTime;
			this.updateTime = updateTime;
			this.paymentTypeDesc = paymentTypeDesc;
			this.statusDesc = statusDesc;
			this.orderItemVOList = orderItemVOList;
			this.shippingVO = shippingVO;
			this.receiverName = receiverName;
			this.imageURI = imageURI;
		}





		public Long getOrderNo() {
			return orderNo;
		}


		public void setOrderNo(Long orderNo) {
			this.orderNo = orderNo;
		}


		public BigDecimal getPayment() {
			return payment;
		}


		public void setPayment(BigDecimal payment) {
			this.payment = payment;
		}


		public Integer getPaymentType() {
			return paymentType;
		}


		public void setPaymentType(Integer paymentType) {
			this.paymentType = paymentType;
		}


		public Integer getPostage() {
			return postage;
		}


		public void setPostage(Integer postage) {
			this.postage = postage;
		}


		public Integer getStatus() {
			return status;
		}


		public void setStatus(Integer status) {
			this.status = status;
		}


		public Integer getShippingId() {
			return shippingId;
		}


		public void setShippingId(Integer shippingId) {
			this.shippingId = shippingId;
		}


		public String getPaymentTime() {
			return paymentTime;
		}


		public void setPaymentTime(String paymentTime) {
			this.paymentTime = paymentTime;
		}


		public String getSendTime() {
			return sendTime;
		}


		public void setSendTime(String sendTime) {
			this.sendTime = sendTime;
		}


		public String getEndTime() {
			return endTime;
		}


		public void setEndTime(String endTime) {
			this.endTime = endTime;
		}


		public String getCloseTime() {
			return closeTime;
		}


		public void setCloseTime(String closeTime) {
			this.closeTime = closeTime;
		}


		public String getCreateTime() {
			return createTime;
		}


		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}


		public String getUpdateTime() {
			return updateTime;
		}


		public void setUpdateTime(String updateTime) {
			this.updateTime = updateTime;
		}



		public ShippingVO getShippingVO() {
			return shippingVO;
		}


		public void setShippingVO(ShippingVO shippingVO) {
			this.shippingVO = shippingVO;
		}
		
		
		public String getStatusDesc() {
			return statusDesc;
		}


		public void setStatusDesc(String statusDesc) {
			this.statusDesc = statusDesc;
		}


		public String getPaymentTypeDesc() {
			return paymentTypeDesc;
		}


		public void setPaymentTypeDesc(String paymentTypeDesc) {
			this.paymentTypeDesc = paymentTypeDesc;
		}


		public String getImageURI() {
			return imageURI;
		}


		public void setImageURI(String imageURI) {
			this.imageURI = imageURI;
		}
		
		public String getReceiverName() {
			return receiverName;
		}


		public void setReceiverName(String receiverName) {
			this.receiverName = receiverName;
		}
		
		
		 public List<OrderItemVO> getOrderItemVOList() {
				return orderItemVOList;
			}


		public void setOrderItemVOList(List<OrderItemVO> orderItemVOList) {
			this.orderItemVOList = orderItemVOList;
		}
}
