package com.lzz.java.aio.socket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class ARead {
	
	public static void read(final AsynchronousSocketChannel socket) {
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
	
}
