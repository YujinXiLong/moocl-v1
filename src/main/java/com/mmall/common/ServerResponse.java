package com.mmall.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse <T> {
		private int status;
		private String msg;
		private T data;
		
		
		@JsonIgnore
		public boolean isSuccess() {
			return this.status == ResponseCode.SUCCESS.getCode();
		}
		
		private	ServerResponse(int status){
			this.status = status;
		}
		
		private ServerResponse(int status,String msg){//---new ServerResponse<>(2, new String ("sad"))
			this.status = status;						//---new ServerResponse<>(2, "sad");
			this.msg = msg;
		//	System.out.println("asd");
		}
		private ServerResponse(int status,T data) {//-------new ServerResponse<>(2,除了上面的任意类型());
			this.status = status;					//如果传递的数据（data）为 String 怎么办; 	看下面
			this.data = data;
			System.out.println("T");
		}
		
		private ServerResponse(int status,String msg,T data) {
			this.status = status;
			this.data = data;
			this.msg = msg;
		}
		
		//用于jsckson 等需要----------为什么不要set呢，因为有构造函数
		public int getStatus() {
			return status;
		}

		public String getMsg() {
			return msg;
		}

		public T getData() {
			return data;
		}
		
		public static <T> ServerResponse<T> creatBySuccess() {
			//return new ServerResponse(this.status);报错，参数要是静态的
			return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
		}
		
		public static <T> ServerResponse<T> creatBySuccessMsg(String msg) {
			return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
		}
		
		public static <T> ServerResponse<T> creatBySuccess(T data) {
			return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
		}
		
		public static <T> ServerResponse<T> creatBySuccess(String msg,T data) {
			return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
		}
		
		public static <T> ServerResponse<T> creatByError() {
			return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDesc());
		}
		
		public static <T> ServerResponse<T> creatByError(String msg) {
			return new ServerResponse<T>(ResponseCode.ERROR.getCode(),msg);
		}
		//用于做扩展，错误有很多种，对应的编码有很多种，但是ResponseCode不可能全部写上，
		public static <T> ServerResponse<T> creatByError(int code,String msg) {
			return new ServerResponse<T>(code,msg);
		}
		
		
		private static final ObjectMapper MAPPER = new ObjectMapper();
		public static String objectToJson(Object data) {
	    	try {
				String string = MAPPER.writeValueAsString(data);
				return string;
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
	    	return null;
	    }
		
		
}
