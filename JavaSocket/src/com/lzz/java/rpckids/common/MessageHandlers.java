package com.lzz.java.rpckids.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息处理器
 * @author lzz
 * @date 2018年5月11日
 * @version 1.0
 */
public class MessageHandlers {

	private Map<String, IMessageHandler<?>> handlers = new HashMap<>();
	private IMessageHandler<MessageInput> defaultHandler;

	public void register(String type, IMessageHandler<?> handler) {
		handlers.put(type, handler);
	}

	public MessageHandlers defaultHandler(IMessageHandler<MessageInput> defaultHandler) {
		this.defaultHandler = defaultHandler;
		return this;
	}

	public IMessageHandler<MessageInput> defaultHandler() {
		return defaultHandler;
	}

	public IMessageHandler<?> get(String type) {
		IMessageHandler<?> handler = handlers.get(type);
		return handler;
	}

}
