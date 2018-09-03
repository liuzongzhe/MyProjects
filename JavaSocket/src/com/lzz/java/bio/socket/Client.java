package com.lzz.java.bio.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

/**
 * 读取用户键盘输入，并写入Socket的输出流
 * @author lzz
 * @date 2018年5月3日
 * @version 1.0
 */
public class Client {

	private static final String IP = "127.0.0.1";
	private static final int SERVER_PORT = 30000;
	private static final int TIME_OUT = 10000;
	
	private Socket s;
	private PrintStream ps;
	private BufferedReader br;
	private BufferedReader keyIn;
	
	/**
	 * 初始化方法
	 */
	public void init() {
		try {
			//创建客户端的Socket
			s = new Socket();
			//设置IP，端口，超时时间
			s.connect(new InetSocketAddress(IP, SERVER_PORT), TIME_OUT);
			
			//获取该Socket对应的输出流
			ps = new PrintStream(s.getOutputStream());
			//获取该Socket对应的输入流
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			//读取键盘输入
			keyIn = new BufferedReader(new InputStreamReader(System.in));
			
			String tip = "";
			//采用循环不断地弹出对话框要求输入用户名
			while (true) {
				//弹出提示框
				String username = JOptionPane.showInputDialog(tip + "输入用户名");
				//在用户名前后增加协议字符串后发送
				ps.println(Protocol.USER_ROUND + username + Protocol.USER_ROUND);
				//读取服务器的响应
				String result = br.readLine();
				//判断用户名非空
				if (result.equals(Protocol.NAME_NULL)) {
					tip = "用户名不能为空！请重新";
					continue;
				}
				//判断用户名重复
				if (result.equals(Protocol.NAME_REP)) {
					tip = "用户名重复！请重新";
					continue;
				}
				//如果服务器返回登录成功，则结束循环
				if (result.equals(Protocol.LOGIN_SUCCESS)) {
					System.out.println(username + "_登录成功");
					break;
				}
			}
		//关闭网络资源，并退出程序
		} catch (UnknownHostException e) {
			System.out.println("找不到远程服务器");
			closeAll();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("网络异常，请重新登录");
			closeAll();
			System.exit(1);
		}		
		//客户端启动ClientThread线程不断的读取来自服务器的数据
		new Thread(new ClientThread(br)).start();
	}
	
	/*
	 * 关闭资源的方法
	 */
	private void closeAll() {
		try {
			if (keyIn != null) {
				keyIn.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (br != null) {
				br.close();
			}
			if (s != null) {
				s.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * 读取键盘输入，并向网络发送
	 */
	private void readAndSend() {
		try {
			String content = null;
			//读取键盘输入的信息
			while ((content = keyIn.readLine().trim()) != null 
				&& (content = keyIn.readLine().trim()) != "\n") {
				/*
				 * 定义信息中带有冒号，则认为是发送私聊信息
				 * 格式： 私聊用户名:私聊信息
				 */
				if (content.indexOf(":") > 0) {
					//包装发送信息格式，加上协议字符串
					ps.println(Protocol.PRIV_ROUND + content.split(":")[0] + 
							Protocol.SPLIT_SIGN + content.split(":")[1] + Protocol.PRIV_ROUND);
				}
				/*
				 * 否则发送的是公聊信息
				 * 格式：公聊信息
				 */
				else {
					//包装发送信息格式，加上协议字符串
					ps.println(Protocol.MSG_ROUND + content + Protocol.MSG_ROUND);					
				}
			}
		//关闭网络资源，并退出程序
		} catch (Exception e) {
			System.out.println("网络异常，请重新登录");
			closeAll();
			System.exit(1);
		}
	}
	
	public static void main(String[] args) {
		Client client = new Client();
		client.init();
		client.readAndSend();
	}
}
