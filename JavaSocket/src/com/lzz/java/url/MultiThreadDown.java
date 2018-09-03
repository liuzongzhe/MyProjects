package com.lzz.java.url;

/**
 * 
 * @author lzz
 * @date 2018年5月3日
 * @version 1.0
 */
public class MultiThreadDown {

	public static void main(String[] args) throws Exception {
		String path = "http://localhost:8080/user/movie.mkv";//开启图片服务器
		String targetFile = "E:/E/tiaotiao/temp/movie.mkv";
		int threadNum = 4;
		//初始化DownUtil对象
		final DownUtils downUtils = new DownUtils(path, targetFile, threadNum);
		//开始下载		
		downUtils.download();
		
		new Thread() {
			public void run() {
				while (downUtils.getCompleteRate() < 1) {
					//每个0.1秒查询一次任务的完成进度
					System.out.println("已完成" + downUtils.getCompleteRate());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("已完成" + downUtils.getCompleteRate());
			}
		}.start();
	}
}
