package com.lzz.java.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 客户端
 * @author lzz
 * @date 2018年5月3日
 * @version 1.0
 */
public class MyClient {
	/*
	 * 读取用户键盘输入，并写入Socket的输出流
	 */
	public static void main(String[] args) throws IOException {
		//创建客户端的Socket
		Socket s = new Socket();
		//设置IP，端口，超时时间
		s.connect(new InetSocketAddress("127.0.0.1", 30000), 30000);
		//客户端启动ClientThread线程不断的读取来自服务器的数据
		new Thread(new ClientThread(s)).start();
		//获取该Socket对应的输出流
		PrintStream ps = new PrintStream(s.getOutputStream());
		String line = null;
		//不断的读取键盘输入
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while ((line = br.readLine()) != null) {
			//将用户的键盘输入内容写入Socket的输出流
			ps.println(line);
		}
	}
}

/*
 * 读取客户端Socket输入流的内容(服务器发送过来的数据)并在控制台打印
 */
class ClientThread implements Runnable {

	private Socket s;
	public ClientThread(Socket s) {
		this.s = s;
	}
	
	BufferedReader br = null;
	
	@Override
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String content = null;
			while ((content = br.readLine()) != null) {
				System.out.println("client：" + content);
			}
		} catch (Exception e) {
			
		}		
	}	
}
