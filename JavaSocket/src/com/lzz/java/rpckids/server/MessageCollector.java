package com.lzz.java.rpckids.server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzz.java.rpckids.common.IMessageHandler;
import com.lzz.java.rpckids.common.MessageHandlers;
import com.lzz.java.rpckids.common.MessageInput;
import com.lzz.java.rpckids.common.MessageRegistry;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 自定义业务逻辑处理器或者控制器
 * @author lzz
 * @date 2018年5月11日
 * @version 1.0
 */
@Sharable
public class MessageCollector extends ChannelInboundHandlerAdapter {
	
	private final static Logger LOG = LoggerFactory.getLogger(MessageCollector.class);
	// 业务线程池
	private ThreadPoolExecutor executor;
	// 消息处理器
	private MessageHandlers handlers;
	// 消息注册器
	private MessageRegistry registry;
	
	public MessageCollector(MessageHandlers handlers, MessageRegistry registry, int workerThreads) {
		// 业务队列最大1000，避免堆积
        // 如果子线程处理不过来，IO线程也会加入处理业务逻辑(callerRunsPolicy)
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(1000);
		// 给业务线程命名
		ThreadFactory factory = new ThreadFactory() {
			AtomicInteger seq = new AtomicInteger();
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("rpc-" + seq.getAndIncrement());
				return t;
			}
		};
		// 闲置时间超过30秒的线程自动销毁
		this.executor = new ThreadPoolExecutor(1, workerThreads, 30, TimeUnit.SECONDS, queue, factory,
				new CallerRunsPolicy());
		this.handlers = handlers;
		this.registry = registry;
	}

	public void closeGracefully() {
		// 优雅一点关闭，先通知，再等待，最后强制关闭
		this.executor.shutdown();
		try {
			this.executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
		this.executor.shutdownNow();
	}
	
	// 客户端来了一个新链接
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOG.debug("connection comes");
	}
	
	// 客户端走了一个链接
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		LOG.debug("connection leaves");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof MessageInput) {
			// 用业务线程池处理消息
			this.executor.execute(() -> {
				this.handleMessage(ctx, (MessageInput) msg);
			});
		} else {
			LOG.warn("server read error");			
		}
	}

	private void handleMessage(ChannelHandlerContext ctx, MessageInput input) {
		// 业务逻辑在这里
		Class<?> clazz = registry.get(input.getType());
		// 没注册的消息用默认的处理器处理
		if (clazz == null) {
			handlers.defaultHandler().handle(ctx, input.getRequestId(), input);
			return;
		}
		Object o = input.getPayload(clazz);
		// 这里是小鲜的瑕疵，代码外观上比较难看
		@SuppressWarnings("unchecked")
		IMessageHandler<Object> handler = (IMessageHandler<Object>) handlers.get(input.getType());
		if (handler != null) {
			handler.handle(ctx, input.getRequestId(), o);
		} else {
			// 用默认的处理器处理吧
			handlers.defaultHandler().handle(ctx, input.getRequestId(), input);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		LOG.warn("connection error", cause);
	}

}
