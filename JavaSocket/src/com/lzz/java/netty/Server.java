package com.lzz.java.netty;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Netty中，通讯的双方建立连接后，会把数据按照ByteBuf的方式进行传输， 
 * 例如http协议中，就是通过HttpRequestDecoder对ByteBuf数据流进行处理，转换成http的对象。 
 * @author lzz
 * @date 2018年5月10日
 * @version 1.0
 */
public class Server {
    
    private String ip;
    private int port;
       	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Autowired 
	private ServerHandler serverHandler;  

	//程序初始方法入口注解，提示spring这个程序先执行这里
	@PostConstruct
	public void run() throws Exception {
		//EventLoopGroup是用来处理IO操作的多线程事件循环器  
        //bossGroup用来接收进来的连接 
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		//workerGroup用来处理已经被接收的连接
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			//启动 NIO服务的辅助启动类  
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)//把事件循环组器加入引导程序
			 .channel(NioServerSocketChannel.class)//开启socket
			 .option(ChannelOption.SO_BACKLOG, 1024)
			 .childOption(ChannelOption.SO_KEEPALIVE, true)
			 .childHandler(new ChannelInitializer<SocketChannel>() {//加入业务处理器
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					//注册handler
					ChannelPipeline pipeline = ch.pipeline();
					// 以("\n")为结尾分割的 解码器
			        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
			        
			        //字符串解码 和 编码
			        pipeline.addLast("decoder", new StringDecoder()); 
			        pipeline.addLast("encoder", new StringEncoder());
			        
			        //加入自定义的Handler
			        pipeline.addLast("handler", serverHandler);
				}
			});
			System.out.println("Server start：" + port);
			//绑定端口，开始接收进来的连接，等待服务器 socket关闭， 可以简写为
            b.bind(ip, port).sync().channel().closeFuture().sync();
		} finally {
			//Netty优雅退出
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
