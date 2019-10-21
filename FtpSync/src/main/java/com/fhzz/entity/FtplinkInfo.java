package com.fhzz.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

/**
* @Description: ftplinkInfo 实体类:同步任务
* @author: lgs
* @date: 2019-10-12 09:09:41
*/ 
@Component
@Entity
@Table(name = "ftplink_info")
public class FtplinkInfo{

	/**null*/
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", length = 11)
	private Integer id;

	/**null*/
	@Column(name = "REMOTE_IP", length = 30)
	private String remoteIp;

	/**null*/
	@Column(name = "NATIVE_IP", length = 30)
	private String nativeIp;

	/**null*/
	@Column(name = "TIME", length = 40)
	private String time;

	/**null*/
	@Column(name = "CREATER", length = 20)
	private String creater;

	/**null*/
	@Column(name = "NATIVE_PORT", length = 6)
	private String nativePort;

	/**null*/
	@Column(name = "REMOTE_PORT", length = 6)
	private String remotePort;

	/**null*/
	@Column(name = "NATIVE_USERNAME", length = 20)
	private String nativeUsername;

	/**null*/
	@Column(name = "REMOTE_USERNAME", length = 20)
	private String remoteUsername;

	/**null*/
	@Column(name = "NATIVE_PASSWORD", length = 40)
	private String nativePassword;

	/**null*/
	@Column(name = "REMOTE_PASSWORD", length = 40)
	private String remotePassword;

	/**null*/
	@Column(name = "SYNC_TIME", length = 10)
	private String syncTime;

	/**null*/
	@Column(name = "TASK", length = 30)
	private String task;

	public FtplinkInfo(){
		super();
	}
	public FtplinkInfo(Integer id, String remoteIp, String nativeIp, String time, String creater, String nativePort, String remotePort, String nativeUsername, String remoteUsername, String nativePassword, String remotePassword, String syncTime, String task) {
		super();
		this.id = id;
		this.remoteIp = remoteIp;
		this.nativeIp = nativeIp;
		this.time = time;
		this.creater = creater;
		this.nativePort = nativePort;
		this.remotePort = remotePort;
		this.nativeUsername = nativeUsername;
		this.remoteUsername = remoteUsername;
		this.nativePassword = nativePassword;
		this.remotePassword = remotePassword;
		this.syncTime = syncTime;
		this.task = task;
	}
	public void setId(Integer id){
		this.id=id;
	}

	public Integer getId(){
		return id;
	}

	public void setRemoteIp(String remoteIp){
		this.remoteIp=remoteIp;
	}

	public String getRemoteIp(){
		return remoteIp;
	}

	public void setNativeIp(String nativeIp){
		this.nativeIp=nativeIp;
	}

	public String getNativeIp(){
		return nativeIp;
	}

	public void setTime(String time){
		this.time=time;
	}

	public String getTime(){
		return time;
	}

	public void setCreater(String creater){
		this.creater=creater;
	}

	public String getCreater(){
		return creater;
	}

	public void setNativePort(String nativePort){
		this.nativePort=nativePort;
	}

	public String getNativePort(){
		return nativePort;
	}

	public void setRemotePort(String remotePort){
		this.remotePort=remotePort;
	}

	public String getRemotePort(){
		return remotePort;
	}

	public void setNativeUsername(String nativeUsername){
		this.nativeUsername=nativeUsername;
	}

	public String getNativeUsername(){
		return nativeUsername;
	}

	public void setRemoteUsername(String remoteUsername){
		this.remoteUsername=remoteUsername;
	}

	public String getRemoteUsername(){
		return remoteUsername;
	}

	public void setNativePassword(String nativePassword){
		this.nativePassword=nativePassword;
	}

	public String getNativePassword(){
		return nativePassword;
	}

	public void setRemotePassword(String remotePassword){
		this.remotePassword=remotePassword;
	}

	public String getRemotePassword(){
		return remotePassword;
	}

	public void setSyncTime(String syncTime){
		this.syncTime=syncTime;
	}

	public String getSyncTime(){
		return syncTime;
	}

	public void setTask(String task){
		this.task=task;
	}

	public String getTask(){
		return task;
	}
	@Override
	public String toString() {
		return "FtplinkInfo [id=" + id + ", remoteIp=" + remoteIp + ", nativeIp=" + nativeIp + ", time=" + time + ", creater=" + creater + ", nativePort=" + nativePort + ", remotePort=" + remotePort + ", nativeUsername=" + nativeUsername + ", remoteUsername=" + remoteUsername + ", nativePassword=" + nativePassword + ", remotePassword=" + remotePassword + ", syncTime=" + syncTime + ", task=" + task + "]";
	}
}

