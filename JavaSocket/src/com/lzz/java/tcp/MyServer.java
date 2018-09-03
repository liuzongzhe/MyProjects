package com.lzz.java.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户端
 * 
 * @author lzz
 * @date 2018年5月3日
 * @version 1.0
 */
public class MyServer {

	/*
	 * 负责处理每个Socket通信的线程类
	 */
	public static List<Socket> list = new ArrayList<>();

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(30000);
		while (true) {
			// 阻塞式，等待连接
			Socket s = ss.accept();
			list.add(s);
			// 每当客户端连接后，启动一个ServerThread线程为客户端服务
			new Thread(new ServerThread(s)).start();
		}
	}
}

/*
 * 负责读取客户端的数据
 */
class ServerThread implements Runnable {

	private Socket s;

	public ServerThread(Socket s) {
		this.s = s;
	}

	BufferedReader br = null;

	// 定义读取客户端数据的方法
	private String readFromClient() {
		try {
			return br.readLine();
		} catch (IOException e) {
			// 客户端出现异常,删除该Socket
			MyServer.list.remove(s);
		}
		return null;
	}

	@Override
	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String content = null;
			// 不断的读取Socket中客户端发来的数据
			while ((content = readFromClient()) != null) {
				PrintStream ps = new PrintStream(s.getOutputStream());
				ps.println(content);
				System.out.println("server：" + content);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}