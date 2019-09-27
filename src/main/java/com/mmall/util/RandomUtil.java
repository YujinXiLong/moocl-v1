package com.mmall.util;

import java.util.Random;

public class RandomUtil {
	public static String getName(){
		Long tem = System.currentTimeMillis();
		Random ran = new Random();
		int num = ran.nextInt(999);
		return tem.toString() + String.format("%03d", num);
	}
	
	public static Long getNO(){
		Long tem = System.currentTimeMillis();
		Random ran = new Random();
		int num = ran.nextInt(999);
		return Long.valueOf(tem.toString() + String.format("%03d", num));
	}
	
	public static void main(String[] args) {
		System.out.println(getName());
	}
}
