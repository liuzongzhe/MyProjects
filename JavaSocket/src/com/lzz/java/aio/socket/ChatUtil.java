package com.lzz.java.aio.socket;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ChatUtil {
	
	private static Random random;
	
	public void sleep() {
		try {
			TimeUnit.SECONDS.sleep(random.nextInt(3));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void sleep(long time) {
		try {
			TimeUnit.SECONDS.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static String getAnswer(String question) {
		String answer = null;
		
		switch (question) {
		case "who":
			answer = "我是小娜\n";
			break;
		case "what":
			answer = "我是来帮你解闷的\n";
			break;
		case "where":
			answer = "我来自外太空\n";
			break;
		default:
			answer = "连接正常，请输入 who， 或者what， 或者where";
		}
		
		return answer;
	}
}