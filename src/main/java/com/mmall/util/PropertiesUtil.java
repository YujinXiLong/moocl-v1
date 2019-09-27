package com.mmall.util;

import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertiesUtil {
	private static Logger logger = LoggerFactory.getLogger(Properties.class);
	private static Properties properties;
	
	static {
		properties = new Properties();
		//把文件流转换为字符流，用utf-8的编码，将数据读到properties（内存中）
		try {
			properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream("resource/ftp.properties"), "UTF-8"));
		} catch (Exception e) {
			logger.error("错误：", e);
		}
	}
	
	public static String getProperty(String key) {
		if (key!=null) {
			if (StringUtils.isBlank(key.trim())) {
				return null;
			}
		}
		return properties.getProperty(key.trim());
	}
	
	public static String getProperty(String key,String defaultValue) {
		if (StringUtils.isBlank(key)) {
			return defaultValue;
		}
		return getProperty(key);
	}
	
}


