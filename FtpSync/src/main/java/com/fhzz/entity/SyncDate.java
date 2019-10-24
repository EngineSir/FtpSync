package com.fhzz.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

/**
* @Description: syncDate 实体类:ftp连接信息
* @author: lgs
* @date: 2019-10-06 13:50:35
*/ 
@Component
@Entity
@Table(name = "sync_date")
@IdClass(SyncIdClass.class)		//指定联合主键
public class SyncDate{

	
	@Id
	@Column(name = "ID", length = 30)
	private Integer id;

	
	@Id
	@Column(name = "PATH", length = 100)
	private String path;

	
	@Column(name = "TIME", length = 20)
	private long time;

	public SyncDate(){
		super();
	}
	public SyncDate(Integer id, String path, long time) {
		super();
		this.id = id;
		this.path = path;
		this.time = time;
	}
	public void setId(Integer id){
		this.id=id;
	}

	public Integer getId(){
		return id;
	}

	public void setPath(String path){
		this.path=path;
	}

	public String getPath(){
		return path;
	}

	public void setTime(long time){
		this.time=time;
	}

	public long getTime(){
		return time;
	}
	@Override
	public String toString() {
		return "SyncDate [id=" + id + ", path=" + path + ", time=" + time + "]";
	}
}

