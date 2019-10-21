package com.fhzz.entity;

import java.io.Serializable;
/**
 * jpa 联合主键类
 * @author Engine
 *
 */
public class SyncIdClass implements Serializable{
	private Integer id;

	private String path;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public String toString() {
		return "SyncIdClass [id=" + id + ", path=" + path + "]";
	}
	
}
