package com.lzz.java.rpckids.demo;

import java.util.ArrayList;
import java.util.List;

import com.lzz.java.rpckids.common.IMessageHandler;
import com.lzz.java.rpckids.common.MessageOutput;
import com.lzz.java.rpckids.server.RPCServer;

import io.netty.channel.ChannelHandlerContext;

class FibRequestHandler implements IMessageHandler<Integer> {
	private List<Long> fibs = new ArrayList<>();
	{
		fibs.add(1L); // fib(0) = 1
		fibs.add(1L); // fib(1) = 1
	}
	@Override
	public void handle(ChannelHandlerContext ctx, String requestId, Integer n) {
		for (int i = fibs.size(); i < n + 1; i++) {
			long value = fibs.get(i - 2) + fibs.get(i - 1);
			fibs.add(value);
		}
		// 输出响应
		ctx.writeAndFlush(new MessageOutput(requestId, "fib_res", fibs.get(n)));
	}
}

class ExpRequestHandler implements IMessageHandler<ExpRequest> {
	@Override
	public void handle(ChannelHandlerContext ctx, String requestId, ExpRequest message) {
		int base = message.getBase();
		int exp = message.getExp();
		long start = System.nanoTime();
		long res = 1;
		for (int i = 0; i < exp; i++) {
			res *= base;
		}
		long cost = System.nanoTime() - start;
		// 输出响应
		ctx.writeAndFlush(new MessageOutput(requestId, "exp_res", new ExpResponse(res, cost)));
	}
}

/**
 * 斐波那契和指数计算处理
 * 使用rpckids提供的远程RPC服务，用于计算斐波那契数和指数，客户端通过rpckids提供的RPC客户端向远程服务传送参数，并接受返回结果，然后呈现出来。
 * @author lzz
 * @date 2018年5月11日
 * @version 1.0
 */
public class DemoServer {

	public static void main(String[] args) {
		RPCServer server = new RPCServer("localhost", 8888, 2, 16);
		server.service("fib", Integer.class, new FibRequestHandler())
			  .service("exp", ExpRequest.class, new ExpRequestHandler());
		server.start();
	}

}
