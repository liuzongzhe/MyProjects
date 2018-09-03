package com.lzz.java.bio.socket;

/**
 * 通信接口协议
 * @author lzz
 * @date 2018年5月3日
 * @version 1.0
 */
public interface Protocol {

	//定义协议字符串的长度
	int PROTOCOL_LEN = 3;
	//协议字符串约束信息
	String MSG_ROUND = "@@@";
	String USER_ROUND = "###";
	String PRIV_ROUND = "$$$";
	String SPLIT_SIGN = "%%%";
	String LOGIN_SUCCESS = "&&&";
	String NAME_NULL = "+++";
	String NAME_REP = "***";
}
