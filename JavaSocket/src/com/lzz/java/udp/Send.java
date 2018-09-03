package com.lzz.java.udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * UDP-发送端
 * @author lzz
 * @date 2018年5月5日
 * @version 1.0
 */
public class Send implements Runnable {
	
	private static final String SEND_IP = "192.168.1.103";
	private static final Integer PORT = 10000;
	
	private DatagramSocket ds;
	
	//建立UDP的Socket服务
	public Send(DatagramSocket ds) {
		this.ds = ds;
	}
	
	@Override
	public void run() {
		BufferedReader br = null;
		try {
			//读取键盘输入
			br = new BufferedReader(new InputStreamReader(System.in));
			String line = null;
			//不断读取，直到输入的是88退出聊天
			while ((line = br.readLine()) != null) {
				//将要发送的数据封装到数据包
				byte[] buf = line.getBytes();
				DatagramPacket dp = 
						new DatagramPacket(buf, buf.length, InetAddress.getByName(SEND_IP), PORT);
				//发送数据包
				ds.send(dp);
				//退出聊天
				if (line.equals("88")) {
					break;
				}
			}
		} catch (IOException e) {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e1) {}
			}
			if (ds != null) {
				ds.close();				
			}
			e.printStackTrace();
		}
	}
}
