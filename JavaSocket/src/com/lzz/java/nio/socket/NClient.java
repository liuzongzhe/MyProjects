package com.lzz.java.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * 客户端：主线程负责初始化，另一个线程负责读取服务器返回的数据
 * @author lzz
 * @date 2018年5月6日
 * @version 1.0
 */
public class NClient {
	
	//指定服务端的IP和端口
	private static final String IP = "127.0.0.1";
	private static final int PORT = 30000;
	
	//定义编码解码的字符集对象
	private Charset charset = Charset.forName("UTF-8");
	
	public static void main(String[] args) throws IOException {
		new NClient().init();
	}
	
	//用于检测所有Channel状态的Selector
	//Channel包括：ServerSocketChannel和SocketChannel
	private Selector selector = null;
	
	//客户端SocketChannel
	private SocketChannel socket = null;
	
	public void init() throws IOException {
		selector = Selector.open();
		//获取未绑定的SocketChannel实例,并指定的连接服务端的IP和PORT
		socket = SocketChannel.open(new InetSocketAddress(IP, PORT));
		//设置Socket非阻塞模式
		socket.configureBlocking(false);
		//注册到Selector对象
		socket.register(selector, SelectionKey.OP_READ);
		//启动多线程，读取服务器端数据
		new Thread(new NClientThread()).start();
		//创建键盘输入流
		@SuppressWarnings("resource")
		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine()) {
			//读取键盘输入
			String line = sc.nextLine();
			//输出到socket流中
			socket.write(charset.encode(line));
		}
	}
	
	private class NClientThread implements Runnable {
		
		private static final int BUFFER_SIZE = 1024;
		
		@Override
		public void run() {
			try {
				while (selector.select() > 0) {
					//遍历每个有可用IO操作的Channel对应的SelectionKey
					for (SelectionKey sKey : selector.selectedKeys()) {
						//删除正在处理的SelectionKey
						selector.selectedKeys().remove(sKey);
						//如果该SelectionKey中对应的Channel有可读的数据
						if (sKey.isReadable()) {
							//获取该SelectionKey对应的Channel
							SocketChannel socket = (SocketChannel) sKey.channel();
							ByteBuffer buff = ByteBuffer.allocate(BUFFER_SIZE);
							String content = "";
							//开始读取数据
							while (socket.read(buff) > 0) {
								socket.read(buff);
								buff.flip();
								content += charset.decode(buff);
							}
							//打印数据
							System.out.println(content);
							//为下一次读取做准备
							sKey.interestOps(SelectionKey.OP_READ);
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
