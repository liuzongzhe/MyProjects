package com.lzz.java.bio.socket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * 扩展map:
 * 根据value获取key
 * 根据value删除key
 * 不允许value重复
 * @author lzz
 * @date 2018年5月3日
 * @version 1.0
 */
public class MyMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 1L;

	/**
	 * 根据value删除key
	 * @param value
	 */
	public void removeByValue(V value) {
		for (K key : keySet()) {
			if (value == get(key)) {
				remove(key);
				break;
			}
		}
	}
	
	/**
	 * 获取所有的key
	 * @return
	 */
	public Set<V> valueSet() {
		Set<V> set = new HashSet<>();
		for (K key : keySet()) {
			set.add(get(key));
		}
		return set;
	}
	
	/**
	 * 根据value获取指定的key
	 * @param value
	 * @return
	 */
	public K getKeyByValue(V value) {
		for (K key : keySet()) {
			if (get(key).equals(value) && get(key)==value) {
				return key;
			}
		}
		return null;
	}
	
	/**
	 * 重写put方法，不允许value重复
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public V put(K key, V value) {
		for (V val : valueSet()) {
			if (val.equals(value) && val.hashCode() == value.hashCode()) {
				throw new RuntimeException("MyMap实例中不允许有重复的value");
			}
		}
		return super.put(key, value);
	}
}
