package com.lzz.java.rpckids.common;

import java.util.UUID;

public class RequestId {
	
	public static String getRequestId() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
	}
}
