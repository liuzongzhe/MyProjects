package com.lzz.java.aio.socket;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.CharacterCodingException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 异步写操作
 * @author lzz
 * @date 2018年5月8日
 * @version 1.0
 */
public class AWrite {
	
	// 写队列，因为当前一个异步写调用还没完成之前，调用异步写会抛WritePendingException
	// 所以需要一个写队列来缓存要写入的数据，这是AIO比较坑的地方
	private final static Queue<ByteBuffer> queue = new LinkedList<>();
	private static boolean writing = false;
	
	/**
	 * 将缓冲区写入通道。 该调用是异步的
	 * 因此缓冲区在传递缓冲区后不能安全地修改。
	 * @param socket
	 * @param buffer 
	 * @throws CharacterCodingException 
	 */
	public static void write(AsynchronousSocketChannel socket, String message) 
			throws CharacterCodingException {
		ByteBuffer buffer = CharsetUtil.encode(message);
		boolean threadShouldWrite = false;
		synchronized (queue) {
			queue.add(buffer);
			// 目前没有线程写入，使这个线程发送一个写操作
			if (!writing) {
				writing = true;
				threadShouldWrite = true;
			}
		}
		if (threadShouldWrite) {
			writeFromQueue(socket);
		}
	}
	
	private static void writeFromQueue(AsynchronousSocketChannel socket) {
		ByteBuffer buffer;		
		synchronized (queue) {
			buffer = queue.poll();
			if (buffer == null) {
				writing = false;
			}
		}
		// 缓冲区中没有新的数据要写入
		if (writing) {
			writeBuffer(socket, buffer);
		}
	}
	
	private static void writeBuffer(final AsynchronousSocketChannel socket, ByteBuffer buffer) {	
		socket.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
			@Override
			public void completed(Integer result, ByteBuffer buffer) {
				if (!buffer.hasRemaining()) {
					// 回调检查是否有新数据要写入
					writeFromQueue(socket);
				} else {
					// 写入到socket流
					socket.write(buffer, buffer, this);
				}
			}
			@Override
			public void failed(Throwable e, ByteBuffer attachment) {
				System.out.println("socket write failed: " + e);
			}
		});
	}
}
