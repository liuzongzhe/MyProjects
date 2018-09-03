package com.lzz.java.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * 服务器：主线程负责初始化，另一个线程处理客户端请求
 * @author lzz
 * @date 2018年5月6日
 * @version 1.0
 */
public class NServer {
	
	private static final String IP = "127.0.0.1";
	private static final int PORT = 30000;
	
	//定义编码解码的字符集对象
	private Charset charset = Charset.forName("UTF-8");
	
	public static void main(String[] args) throws IOException {
		new NServer().init();
	}
	
	//用于监测所有Channel状态的Selector
	//Channel包括：ServerSocketChannel和SocketChannel
	private Selector selector = null;	
	
	//服务器端ServerSocketChannel
	private ServerSocketChannel server = null;
	
	public void init() throws IOException {
		selector = Selector.open();
		//获取未绑定的ServerSocketChannel实例,并绑定到指定的IP
		server = ServerSocketChannel.open();		
		server.bind(new InetSocketAddress(IP, PORT));
		//设置ServerSocket非阻塞模式
		server.configureBlocking(false);
		//注册到Selector对象
		server.register(selector, SelectionKey.OP_ACCEPT);
		//调用多线程处理客户端服务
		new Thread(new NServerThread()).start();
 	}
	
	private class NServerThread implements Runnable {
		
		private static final int BUFFER_SIZE = 1024;
		
		@Override
		public void run() {
			try {
				//依次处理Selector上的每个SelectionKey
				//SelectionKey中保存了当前请求的Channel(SocketChannel)
				while (selector.select() > 0) {
					for (SelectionKey sKey : selector.selectedKeys()) {
						//从Selector上的key集中删除正在处理的SelectionKey
						selector.selectedKeys().remove(sKey);
						//如果SelectionKey对应的Channel包含客户端的连接请求
						if (sKey.isAcceptable()) {
							handleAccept(sKey);
						}
						//如果SelectionKey对应的Channel有数据需要读取
						if (sKey.isReadable()) {
							handleRead(sKey);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void handleAccept(SelectionKey sKey) throws IOException {
			//得到服务器端的SocketChannel
			SocketChannel socket = server.accept();
			//设置非阻塞模式
			socket.configureBlocking(false);
			//注册到selector		
			socket.register(selector, SelectionKey.OP_READ);
			//为下一次接收请求 做准备
			sKey.interestOps(SelectionKey.OP_ACCEPT);
		}
		
		private void handleRead(SelectionKey sKey) throws IOException {
			//获取该SelectionKey对应的Channel
			SocketChannel socket = (SocketChannel) sKey.channel();
			//获取该SelectionKey对应的buff并重置
			ByteBuffer buff = ByteBuffer.allocate(BUFFER_SIZE);
			buff.clear();
			//开始读数据
			try {
				while (socket.read(buff) > 0) {
					//将buff转换为读状态
					buff.flip();
					//打印读取到的数据
					String receiceStr = charset.decode(buff).toString();
					System.out.println("接受的数据：" + receiceStr);
					//将数据返回给客户端
					String sendStr = "服务器响应：" + receiceStr;
					buff = charset.encode(sendStr);
					socket.write(buff);
					//为下一次读取做准备
					sKey.interestOps(SelectionKey.OP_READ);
				}
			} catch (IOException e) {
				//客户端出现异常，从Selector中取消SelectionKey的注册
				System.out.println("客户端已关闭");
				sKey.cancel();
				if (sKey.channel() != null) {
					sKey.channel().close();
				}
			}
		}	
	}
}


