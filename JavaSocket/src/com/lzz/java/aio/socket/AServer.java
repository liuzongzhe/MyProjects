package com.lzz.java.aio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;

/**
 * 服务端
 * @author lzz
 * @date 2018年5月7日
 * @version 1.0
 */
public class AServer {
	private static final int PORT = 30000;
	private final AsynchronousServerSocketChannel server;
	
	public static void main(String[] args) throws IOException {
		new AServer().listen();
	}
	
	public AServer() throws IOException {
		// 设置线程数为CPU核数
		AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup
				.withFixedThreadPool(Runtime.getRuntime().availableProcessors(), Executors.defaultThreadFactory());
		// 实例化server
		server = AsynchronousServerSocketChannel.open(channelGroup);
		// 重用端口
		server.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		// 绑定端口并设置连接请求队列长度
		server.bind(new InetSocketAddress(PORT), 80);
	}
	
	public void listen() {
		System.out.println(Thread.currentThread().getName() + ": run in listen method");
		/*
		 *  开始接受第一个连接请求
		 */
		server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
			// 当实际IO操作完成时触发该方法
			@Override
			public void completed(AsynchronousSocketChannel socket, Object attachment) {
				/*
				 *  先安排处理下一个连接请求，异步非阻塞调用，所以不用担心挂住了，这里传入this是个地雷，小心多线程
				 */
				server.accept(null, this);
				/*
				 * 处理连接读写
				 */
				handle(socket);
			}
			// 服务器接受连接失败处理
			@Override
			public void failed(Throwable e, Object attachment) {
				System.out.println("server accept failed: " + e);
			}
			
			private void handle(final AsynchronousSocketChannel socket) {
				System.out.println(Thread.currentThread().getName() + ": run in handle method");
				// 每个AsynchronousSocketChannel，分配一个缓冲区
				final ByteBuffer buffer = ByteBuffer.allocate(1024);
				/*
				 * 异步调用OS读取接收到的消息
				 */
				socket.read(buffer, null, new CompletionHandler<Integer, Object>() {					
					@Override
					public void completed(Integer result, Object attachment) {
						// 异步读取完成后处理
						if (result > 0) {
							try {
								// 从buffer中取数据做好准备
								buffer.flip();
								// 获取接收到的数据
								String receive = CharsetUtil.decode(buffer).toString();
								System.out.println(Thread.currentThread().getName() + "---" + receive);
								/*
								 * 包装数据
								 */
								String send = ChatUtil.getAnswer(receive);
								/*
								 * 写入也是异步调用，使用传入CompletionHandler对象的方式来处理写入结果
								 */
								AWrite.write(socket, send);
								// 重置缓冲区
								buffer.clear();
							} catch (IOException e) {
								e.printStackTrace();
							} 
						} else {
							try {
								// 如果客户端关闭socket，那么服务器也需要关闭，否则浪费CPU
								if (socket != null) {
									socket.close();									
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						/*
						 *  异步调用OS处理下个读取请求，传入this是个地雷，小心多线程
						 */
						socket.read(buffer, null, this);
					}
					// 读失败处理
					@Override
					public void failed(Throwable exc, Object attachment) {
						System.out.println("server read failed: " + exc);
						try {
							if (socket != null) {
								socket.close();
							} 
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
			
		});
	}	
	
}