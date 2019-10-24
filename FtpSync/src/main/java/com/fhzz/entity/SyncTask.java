package com.fhzz.entity;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import java.sql.*;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.stereotype.Component;

/**
* @Description: syncTask 实体类:同步任务
* @author: lgs
* @date: 2019-10-11 11:00:15
*/ 
@Component
@Entity
@Table(name = "sync_task")
public class SyncTask{

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", length = 11)
	private Integer id;

	
	@Column(name = "REMOTE_PATH", length = 100)
	private String remotePath;

	
	@Column(name = "NATIVE_PATH", length = 100)
	private String nativePath;

	
	@Column(name = "TIME", length = 40)
	private String time;

	
	@Column(name = "FTP_ID", length = 20)
	private String ftpId;

	
	@Column(name = "TASK_NAME", length = 10)
	private String taskName;

	
	@Column(name = "FLAG", length = 2)
	private Integer flag;
	
	
	public SyncTask(){
		super();
	}
	public SyncTask(Integer id, String remotePath, String nativePath, String time, String ftpId, String taskName) {
		super();
		this.id = id;
		this.remotePath = remotePath;
		this.nativePath = nativePath;
		this.time = time;
		this.ftpId = ftpId;
		this.taskName = taskName;
	}
	public void setId(Integer id){
		this.id=id;
	}

	public Integer getId(){
		return id;
	}

	public void setRemotePath(String remotePath){
		this.remotePath=remotePath;
	}

	public String getRemotePath(){
		return remotePath;
	}

	public void setNativePath(String nativePath){
		this.nativePath=nativePath;
	}

	public String getNativePath(){
		return nativePath;
	}

	public void setTime(String time){
		this.time=time;
	}

	public String getTime(){
		return time;
	}

	public void setFtpId(String ftpId){
		this.ftpId=ftpId;
	}

	public String getFtpId(){
		return ftpId;
	}

	public void setTaskName(String taskName){
		this.taskName=taskName;
	}

	public String getTaskName(){
		return taskName;
	}
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	@Override
	public String toString() {
		return "SyncTask [id=" + id + ", remotePath=" + remotePath + ", nativePath=" + nativePath + ", time=" + time
				+ ", ftpId=" + ftpId + ", taskName=" + taskName + ", flag=" + flag + "]";
	}
}

