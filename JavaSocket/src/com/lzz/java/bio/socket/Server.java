package com.lzz.java.bio.socket;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 负责处理每个Socket通信的线程类
 * @author lzz
 * @date 2018年5月3日
 * @version 1.0
 */
public class Server {

	private static final int SERVER_PORT = 30000;
	
	public static MyMap<String, PrintStream> clientMap = new MyMap<>();
	
	public void init() {
		try {
			//建立监听的ServerSocket
			@SuppressWarnings("resource")
			ServerSocket ss = new ServerSocket(SERVER_PORT);
			//不断地接收来自客户端的请求
			while (true) {
				//阻塞式，等待连接
				Socket s = ss.accept();
				//每当客户端连接后，启动一个ServerThread线程为客户端服务
				new Thread(new ServerThread(s)).start();
			}
		} catch (Exception e) {
			System.out.println("服务器启动失败");
		}
	}
	
	public static void main(String[] args) {
		new Server().init();
	}
}
