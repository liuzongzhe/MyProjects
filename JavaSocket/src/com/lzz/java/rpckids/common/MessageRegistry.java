package com.lzz.java.rpckids.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息类型注册器
 * @author lzz
 * @date 2018年5月11日
 * @version 1.0
 */
public class MessageRegistry {
	private Map<String, Class<?>> clazzes = new HashMap<>();

	public void register(String type, Class<?> clazz) {
		clazzes.put(type, clazz);
	}

	public Class<?> get(String type) {
		return clazzes.get(type);
	}
}
