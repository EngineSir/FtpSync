package com.fhzz.tool;

import java.util.List;

import com.google.common.collect.Lists;
/**
 *	 存储分页数据结果
 * @author Engine
 *
 * @param <T>
 */
public class PageResult<T> {

	private List<T> data = Lists.newArrayList();

	private int total = 0;

	public PageResult() {
		super();
	}

	public PageResult(List<T> data, int total) {
		super();
		this.data = data;
		this.total = total;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	@Override
	public String toString() {
		return "PageResult [data=" + data + ", total=" + total + "]";
	}

}
