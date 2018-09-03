package com.lzz.java.aio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.util.Scanner;
import java.util.concurrent.Executors;

/**
 * 客户端
 * @author lzz
 * @date 2018年5月7日
 * @version 1.0
 */
public class AClient implements Runnable {
	//服务器端IP和端口
	private static final String IP = "127.0.0.1";
	private static final int PORT = 30000;
	private AsynchronousChannelGroup channelGroup;
	private AsynchronousSocketChannel socket;
	
	public AClient() throws IOException, InterruptedException {	
		// 设置线程数为CPU核数
		channelGroup = AsynchronousChannelGroup
				.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
		// 在默认channel group下创建一个socket channel
		socket = AsynchronousSocketChannel.open(channelGroup);
		// 设置Socket选项
		socket.setOption(StandardSocketOptions.TCP_NODELAY, true);
		socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
		socket.setOption(StandardSocketOptions.SO_REUSEADDR, true);
	}
	
	public void shutdown() {
		if (channelGroup != null) {
			channelGroup.shutdown();
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// 只能跑一个线程，第二个线程connect会挂住，暂时不明原因
		AClient client = new AClient();
		// 创建多线程模拟多个客户端，模拟失败，无效
		// 只能通过命令行同时运行多个进程来模拟多个客户端
		System.out.println("start client thread: ");
		new Thread(client).start();
		client.shutdown();
	}
	
	@Override
	public void run() {
		/*
		 * 连接服务器
		 */
		socket.connect(new InetSocketAddress(IP, PORT), null, new CompletionHandler<Void, Void>() {
			final ByteBuffer readBuffer = ByteBuffer.allocateDirect(1024);
			
			@Override
			public void completed(Void result, Void attachment) {
				// 连接成功后, 异步调用OS向服务器写一条消息
				try {
					String send = "isPing";
					AWrite.write(socket, send);
				} catch (CharacterCodingException e) {
					e.printStackTrace();
				}
				// 重置缓冲区
				readBuffer.clear();
				/*
				 * 异步调用OS读取接收到的消息
				 */
				socket.read(readBuffer, null, new CompletionHandler<Integer, Object>() {
					@Override
					public void completed(Integer result, Object attachment) {
						try {
							// 异步读取完成后处理
							if (result > 0) {
								readBuffer.flip();
								String receive = CharsetUtil.decode(readBuffer).toString();
								System.out.println(Thread.currentThread().getName() + "---" + receive);
								/*
								 * 发送数据
								 */
								@SuppressWarnings("resource")
								Scanner sc = new Scanner(System.in);
								String send = sc.nextLine();
								/*
								 *  调用异步写操作
								 */
								AWrite.write(socket, send);
								// 重置缓冲区
								readBuffer.clear();
								/*
								 * 异步调用OS处理下个读取请求，传入this是个地雷，小心多线程
								 */
								socket.read(readBuffer, null, this);									
							} else {
								// 对方已经关闭socket，自己被动关闭，避免空循环
								close();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// 读取失败处理
					@Override
					public void failed(Throwable exc, Object attachment) {
						System.out.println("client read failed: " + exc);
						close();
					}
				});
			}			
			/**
			 * 连接服务器失败处理
			 * @param exc
			 * @param attachment
			 */
			@Override
			public void failed(Throwable exc, Void attachment) {
				System.out.println("client connect to server failed: " + exc);
				close();
			}
		});
	}
	
	private void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
