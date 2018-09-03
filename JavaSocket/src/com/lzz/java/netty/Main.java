package com.lzz.java.netty;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		//加载spirng配置文件
		new ClassPathXmlApplicationContext("classpath:com/lzz/java/netty/server.xml");
	}
}
