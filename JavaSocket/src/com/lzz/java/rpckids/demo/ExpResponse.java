package com.lzz.java.rpckids.demo;

/**
 * 指数RPC的输出
 * @author lzz
 * @date 2018年5月11日
 * @version 1.0
 */
public class ExpResponse {

	private long value;
	private long costInNanos;

	public ExpResponse() {
	}

	public ExpResponse(long value, long costInNanos) {
		this.value = value;
		this.costInNanos = costInNanos;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public long getCostInNanos() {
		return costInNanos;
	}

	public void setCostInNanos(long costInNanos) {
		this.costInNanos = costInNanos;
	}

}
