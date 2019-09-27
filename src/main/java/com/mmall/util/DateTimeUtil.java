package com.mmall.util;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeUtil {
	private static String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	
	public static String DateToStr(Date date,String formator) {
		if (date==null) {
			return StringUtils.EMPTY;//""
		}
		DateTime dateTime = new DateTime(date);
		return dateTime.toString(formator);
	}
	
	public static String DateToStr(Date date) {
			if (date==null) {
				return StringUtils.EMPTY;//""
			}
			DateTime dateTime = new DateTime(date);
			return dateTime.toString(STANDARD_FORMAT);
	}
	
	public static Date StrToDate(String str,String formator) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formator);
		DateTime dateTime = dateTimeFormatter.parseDateTime(str);
		return dateTime.toDate();
	}
	
	public static Date StrToDate(String str) {
		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
		DateTime dateTime = dateTimeFormatter.parseDateTime(str);
		return dateTime.toDate();
	}
	
	
	public static void main(String[] args) {
		System.out.println(DateTimeUtil.DateToStr(new Date(2343242342l)));
		System.out.println(DateTimeUtil.StrToDate("2019-08-15 16:29:19"));
	}
}
