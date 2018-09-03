package com.lzz.java.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client implements Runnable {
	
	public static String host = "127.0.0.1";
    public static int port = 30000;
    public static ClientHandler client = new ClientHandler();

	public static void main(String[] args) throws Exception {
		new Thread(new Client()).start();
	}

	@Override
	public void run() {
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					//这个地方的 必须和服务端对应上。否则无法正常解码和编码
					pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
			        pipeline.addLast("decoder", new StringDecoder());
			        pipeline.addLast("encoder", new StringEncoder());
			        //添加客户端逻辑
					ch.pipeline().addLast(client);
				}
			});
			// 连接服务端
            Channel ch = b.connect(host, port).sync().channel();
            
            // 控制台输入
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            //也可以用while循环
            while (true) {
                String line = null;
				try {
					line = in.readLine();
					if (line == null) {
						continue;
					}
					if (line.equals("88")) {
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
                /*
                 * 向服务端发送在控制台输入的文本 并用"\r\n"结尾
                 */
                ch.writeAndFlush(line + "\r\n");
            }
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			workerGroup.shutdownGracefully();
		}
	}
}