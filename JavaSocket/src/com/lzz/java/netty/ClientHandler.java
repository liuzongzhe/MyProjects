package com.lzz.java.netty;

import java.text.DateFormat;
import java.util.Date;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
	
	/**
	 * tcp连接成功后调用一次
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client active");
	}
	
	/**
	 * 收到服务器消息后调用 
	 * @param ctx
	 * @param receive
	 * @throws Exception
	 */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String receive) throws Exception {
    	//打印服务器响应的数据
    	String time = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG).format(new Date());
        System.out.println(time + "\n" + receive);
    }
    
    /**
     * 服务器异常时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server close");
        super.channelInactive(ctx);
    }
}