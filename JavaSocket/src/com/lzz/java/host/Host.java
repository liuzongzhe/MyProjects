package com.lzz.java.host;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host {

	public static void main(String[] args) throws UnknownHostException {
		String s = InetAddress.getLocalHost().getHostName();
		String s2 = InetAddress.getLocalHost().getCanonicalHostName();
		System.out.println(s2);
	}
}
