package com.lzz.java.rpckids.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzz.java.rpckids.common.IMessageHandler;
import com.lzz.java.rpckids.common.MessageInput;

import io.netty.channel.ChannelHandlerContext;

/**
 * 找不到类型的消息统一使用默认处理器处理
 * @author lzz
 * @date 2018年5月11日
 * @version 1.0
 */
public class DefaultHandler implements IMessageHandler<MessageInput> {

	private final static Logger LOG = LoggerFactory.getLogger(DefaultHandler.class);

	@Override
	public void handle(ChannelHandlerContext ctx, String requesetId, MessageInput input) {
		LOG.error("unrecognized message type {} comes", input.getType());
		ctx.close();
	}

}
