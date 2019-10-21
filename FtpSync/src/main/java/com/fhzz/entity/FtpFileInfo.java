package com.fhzz.entity;
/**
 * ftp文件属性及同步标志
 * @author Engine
 *
 */
public class FtpFileInfo {

	private String name;
	private String updateTime;
	private String type;
	private String size;
	private Integer syncFlag;
	public Integer getSyncFlag() {
		return syncFlag;
	}
	public void setSyncFlag(Integer syncFlag) {
		this.syncFlag = syncFlag;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	@Override
	public String toString() {
		return "FtpFileInfo [name=" + name + ", updateTime=" + updateTime + ", type=" + type + ", size=" + size
				+ ", syncFlag=" + syncFlag + "]";
	}
	
}
