package com.lzz.java.udp;

import java.io.IOException;
import java.net.DatagramSocket;

public class UDPApp {
	
	public static void main(String[] args) throws IOException {
		new UDPApp().chat();
	}
	
	public void chat() throws IOException {
		DatagramSocket send = new DatagramSocket();
		new Thread(new Send(send)).start();
		DatagramSocket rece = new DatagramSocket(10000);
		new Thread(new Rece(rece)).start();
	}
	
}
