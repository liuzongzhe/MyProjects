package com.lzz.java.bio.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * 负责读取客户端的数据
 * @author lzz
 * @date 2018年5月3日
 * @version 1.0
 */
public class ServerThread implements Runnable {
	
	private Socket s;
	//构造器，接收客户端传来的Socket
	public ServerThread(Socket s) {
		this.s = s;
	}
	
	//将读取到的内容去掉前后的协议字符
	private String getRealMsg(String content) {
		return content.substring(Protocol.PROTOCOL_LEN, content.length() - Protocol.PROTOCOL_LEN);
	}
	
	BufferedReader br = null;
	PrintStream ps = null;
	
	@Override
	public void run() {
		try {
			//获取客户端Socket的输入流和输出流
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			ps = new PrintStream(s.getOutputStream());
			String content = null;
			//不断的读取Socket中客户端发来的数据
			while ((content = br.readLine()) != null) {
				/*
				 * 判断是否是用户登录的用户名
				 */
				if (content.startsWith(Protocol.USER_ROUND) && content.endsWith(Protocol.USER_ROUND)) {
					//获取真实信息
					String username = getRealMsg(content);
					//非空校验
					if (username.isEmpty() || username == null) {
						//返回客户端错误信息
						ps.println(Protocol.NAME_NULL);						
					}
					//如果用户名重复
					else if (Server.clientMap.containsKey(username)) {
						//返回客户端重复信息
						ps.println(Protocol.NAME_REP);
					} else {
						System.out.println(username + "_登录成功");
						Server.clientMap.put(username, ps);
						//返回客户端登陆成功
						ps.println(Protocol.LOGIN_SUCCESS);
					}
				} 
				/*
				 * 判断是否是私聊信息,只给特定的输出流（客户端Socket）发送
				 */
				else if (content.startsWith(Protocol.PRIV_ROUND) && content.endsWith(Protocol.PRIV_ROUND)) {
					//得到真实信息
					String userAndMsg = getRealMsg(content);
					//以SPLIT_SIGN分割信息
					String user = userAndMsg.split(Protocol.SPLIT_SIGN)[0];
					String msg = userAndMsg.split(Protocol.SPLIT_SIGN)[1];
					//获取私聊用户对应的输出流，并发送私聊信息
					Server.clientMap.get(user).println(Server.clientMap.getKeyByValue(ps) + ":" + msg);
				}
				/*
				 * 最后是公聊信息,给每个输出流（客户端Socket）都发送
				 */
				else {
					//得到真实信息
					String msg = getRealMsg(content);
					//遍历每个输出流
					for (PrintStream clientPs : Server.clientMap.valueSet()) {
						clientPs.println("公告信息:" + Server.clientMap.getKeyByValue(ps) + ":" + msg);
					}
				}
			}
		//Socket对应的客户端出现问题，将其对应的输出流从map中删除
		} catch (IOException e) {			
			Server.clientMap.removeByValue(ps);
			System.out.println("当前用户量:" + Server.clientMap.size());
			//关闭资源
			try {
				if (br != null) {
					br.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (s != null) {
					s.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
