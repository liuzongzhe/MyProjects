package com.lzz.java.netty;

import java.net.InetAddress;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;

/**
 * 自定义业务逻辑处理器
 * @author lzz
 * @date 2018年5月10日
 * @version 1.0
 */
@Service("ServerHandler")
@Scope("prototype")
@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

	// 获取现有通道，一个通道channel就是一个socket链接在这里
	public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	// 有新链接加入，对外发布消息
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		for (Channel channel : channels) {
			channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 加入\n");
		}
		channels.add(ctx.channel());
	}

	// 有链接断开，对外发布消息
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		for (Channel channel : channels) {
			channel.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 离开\n");
		}
		channels.remove(ctx.channel());
	}

	// 消息读取有两个方法，channelRead和channelRead0，其中channelRead0可以读取泛型，常用
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// 收到消息直接打印输出
		System.out.println(ctx.channel().remoteAddress() + " request : " + msg);
		// 返回客户端消息 
		ctx.writeAndFlush("Server response : recevie success ! \n");
	}

	// 覆盖 channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// 连接成功打印输出
		System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");
		// 返回客户端消息 - 服务的主机
		ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostName() + " service !\n");
		super.channelActive(ctx);
	}
}
