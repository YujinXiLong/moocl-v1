package com.mmall.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
	
	private BigDecimalUtil() {
	}
	
	
	public static BigDecimal add(double A,double B) {
		BigDecimal a = new BigDecimal(Double.toString(A));
		BigDecimal b = new BigDecimal(Double.toString(B));
		return a.add(b);
	}
	public static BigDecimal sub(double A,double B) {
		BigDecimal a = new BigDecimal(Double.toString(A));
		BigDecimal b = new BigDecimal(Double.toString(B));
		return a.subtract(b);
	}
	public static BigDecimal mul(double A,double B) {
		BigDecimal a = new BigDecimal(Double.toString(A));
		BigDecimal b = new BigDecimal(Double.toString(B));
		return a.multiply(b);
	}
	public static BigDecimal div(double A,double B) {
		BigDecimal a = new BigDecimal(Double.toString(A));
		BigDecimal b = new BigDecimal(Double.toString(B));
		return a.divide(b, 2, BigDecimal.ROUND_HALF_UP);
	}
	
	
	public static void main(String[] args) {
		BigDecimal a = new BigDecimal(2);
		BigDecimal b = new BigDecimal(3);
		
		System.out.println(a.divide(b,2,BigDecimal.ROUND_HALF_EVEN));
		//float b = a.floatValue();
	}
}
