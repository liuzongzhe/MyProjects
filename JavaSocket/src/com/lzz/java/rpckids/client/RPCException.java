package com.lzz.java.rpckids.client;

/**
 * 定义客户端异常，用于统一抛出RPC错误
 * @author lzz
 * @date 2018年5月11日
 * @version 1.0
 */
public class RPCException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RPCException(String message, Throwable cause) {
		super(message, cause);
	}

	public RPCException(String message) {
		super(message);
	}
	
	public RPCException(Throwable cause) {
		super(cause);
	}

}
