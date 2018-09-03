package com.lzz.java.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * UDP-接收端
 * @author lzz
 * @date 2018年5月5日
 * @version 1.0
 */
public class Rece implements Runnable {
	
	private DatagramSocket ds;
	
	//建立UDP的Socket服务
	public Rece(DatagramSocket ds) {
		this.ds = ds;
	}
	
	@Override
	public void run() {
		//建立UDP的接收端服务（明确端口号）
		try {
			while (true) {
				//创建数据包，用户存储接收到的数据
				byte[] buf = new byte[1024];
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				//接收数据（阻塞式）
				ds.receive(dp);
				//打印数据
				String ip = dp.getAddress().getHostAddress();
				int port = dp.getPort();
				String str = new String(dp.getData(), 0, dp.getLength());
				System.out.println(ip + ":" + port + ":" + str);
				//判断退出循环
				if (str.equals("88")) {
					System.out.println("退出聊天了");
				}
			}		
		} catch (IOException e) {
			if (ds != null) {
				ds.close();
			}
			e.printStackTrace();
		}
	}
}
