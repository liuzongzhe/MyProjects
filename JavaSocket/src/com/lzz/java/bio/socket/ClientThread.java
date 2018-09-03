package com.lzz.java.bio.socket;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * 读取客户端Socket输入流的内容(服务器发送过来的数据)并在控制台打印
 * @author lzz
 * @date 2018年5月3日
 * @version 1.0
 */
public class ClientThread implements Runnable {
	
	//负责处理输入流的线程
	BufferedReader br = null;
	public ClientThread(BufferedReader br) {
		this.br = br;
	}
	
	@Override
	public void run() {
		try {
			String content = null;
			while ((content = br.readLine()) != null) {
				System.out.println(content);
				/*
				 * 可以添加更加复杂的逻辑
				 */
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
