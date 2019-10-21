package com.fhzz.entity.param;
/**
 * ftp信息参数
 * @author Engine
 *
 */
public class FtpLinkInfoParam {
	private String id;

	private String nick;

	private String remote_ip;

	private String native_ip;

	private String time;

	private String creater;

	private String native_port;

	private String remote_port;

	private String native_username;

	private String remote_username;

	private String native_password;

	private String remote_password;

	private String sync_time;

	private String task;

	public FtpLinkInfoParam(){
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getRemote_ip() {
		return remote_ip;
	}

	public void setRemote_ip(String remote_ip) {
		this.remote_ip = remote_ip;
	}

	public String getNative_ip() {
		return native_ip;
	}

	public void setNative_ip(String native_ip) {
		this.native_ip = native_ip;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public String getNative_port() {
		return native_port;
	}

	public void setNative_port(String native_port) {
		this.native_port = native_port;
	}

	public String getRemote_port() {
		return remote_port;
	}

	public void setRemote_port(String remote_port) {
		this.remote_port = remote_port;
	}

	public String getNative_username() {
		return native_username;
	}

	public void setNative_username(String native_username) {
		this.native_username = native_username;
	}

	public String getRemote_username() {
		return remote_username;
	}

	public void setRemote_username(String remote_username) {
		this.remote_username = remote_username;
	}

	public String getNative_password() {
		return native_password;
	}

	public void setNative_password(String native_password) {
		this.native_password = native_password;
	}

	public String getRemote_password() {
		return remote_password;
	}

	public void setRemote_password(String remote_password) {
		this.remote_password = remote_password;
	}

	public String getSync_time() {
		return sync_time;
	}

	public void setSync_time(String sync_time) {
		this.sync_time = sync_time;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public FtpLinkInfoParam(String id, String nick, String remote_ip, String native_ip, String time, String creater,
			String native_port, String remote_port, String native_username, String remote_username,
			String native_password, String remote_password, String sync_time, String task) {
		super();
		this.id = id;
		this.nick = nick;
		this.remote_ip = remote_ip;
		this.native_ip = native_ip;
		this.time = time;
		this.creater = creater;
		this.native_port = native_port;
		this.remote_port = remote_port;
		this.native_username = native_username;
		this.remote_username = remote_username;
		this.native_password = native_password;
		this.remote_password = remote_password;
		this.sync_time = sync_time;
		this.task = task;
	}

	@Override
	public String toString() {
		return "FtpLinkInfoParam [id=" + id + ", nick=" + nick + ", remote_ip=" + remote_ip + ", native_ip=" + native_ip
				+ ", time=" + time + ", creater=" + creater + ", native_port=" + native_port + ", remote_port="
				+ remote_port + ", native_username=" + native_username + ", remote_username=" + remote_username
				+ ", native_password=" + native_password + ", remote_password=" + remote_password + ", sync_time="
				+ sync_time + ", task=" + task + "]";
	}
	
}
