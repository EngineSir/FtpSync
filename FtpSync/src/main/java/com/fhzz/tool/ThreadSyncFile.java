package com.fhzz.tool;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fhzz.entity.FtplinkInfo;
import com.fhzz.entity.SyncDate;
import com.fhzz.repository.SyncDateRepositoryImpl;
/**
 * 	同步任务线程
 * @author Engine
 *
 */
public class ThreadSyncFile extends TimerTask {

	private FTPClient remoteFtpClient;
	
	private FTPClient nativeFtpClient;
	
	//目录
	private String remotePath;
	
	private String nativePath;
	
	private Integer id;

	//线程内无法使用注解自动注入，只能外部传入赋值
	private SyncDateRepositoryImpl syncDateRepositoryImpl;
	
	  private final Logger log = LoggerFactory.getLogger(ThreadSyncFile.class);

	public ThreadSyncFile() {}
	
	public ThreadSyncFile(FtplinkInfo ftpLinkInfo,String remotePath,String nativePath,SyncDateRepositoryImpl syncDateRepositoryImpl) {
		//super();
		 this.remoteFtpClient=FtpUtil.getFtpClient(ftpLinkInfo.getRemoteIp(), ftpLinkInfo.getRemotePort(), ftpLinkInfo.getRemoteUsername(), ftpLinkInfo.getRemotePassword());
		 this.nativeFtpClient=FtpUtil.getFtpClient(ftpLinkInfo.getNativeIp(), ftpLinkInfo.getNativePort(), ftpLinkInfo.getNativeUsername(), ftpLinkInfo.getNativePassword());
		 this.remotePath = remotePath;
		 this.nativePath=nativePath;
		 this.id=ftpLinkInfo.getId();
		 this.syncDateRepositoryImpl=syncDateRepositoryImpl;
		 this.remoteFtpClient.setControlEncoding("GBK");
		 this.nativeFtpClient.setControlEncoding("GBK");
	}
	
	/**
	 * 	切换目录
	 * @param curDate
	 */
	private void ftpFolderDir(String curRemotePath,String curNativePath) {
		try {
			this.nativeFtpClient.makeDirectory(curNativePath);
			this.nativeFtpClient.changeWorkingDirectory(curNativePath);
			//this.remoteFtpClient.makeDirectory(curDate);
			//切换到需要同步改天的文件夹下
			this.remoteFtpClient.changeWorkingDirectory(curRemotePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
			try {
				//String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
				this.remoteFtpClient.changeWorkingDirectory(this.remotePath);
				this.nativeFtpClient.changeWorkingDirectory(this.nativePath);
				
				//判断本地ftp是否有该文件夹，没有则创建,并切换到该目录下
				//ftpFolderDir(curDate);
				SyncDate syncDate=null;
				if(this.remotePath.equals(this.nativePath)) {
					 syncDate=syncDateRepositoryImpl.getInfoByIdAndPath(this.id, this.remotePath);
				}else {
					 syncDate=syncDateRepositoryImpl.getInfoByIdAndPath(this.id, this.remotePath+this.nativePath);
				}
				
				//判断远程ftp是否已经建立当天文件夹
				//boolean flag=false;
				FTPFile[] remoteFiles = remoteFtpClient.listFiles();
//				for(FTPFile file:remoteFiles) {
//					if(file.isDirectory() && file.getName().equals(curDate)) {
//						flag=true;
//						break;
//					}
//				}
			//	log.info(curDate+" "+flag);
				//if(flag) {
					//进行文件同步
					syncDirFiles(syncDate,this.remotePath+"/",this.nativePath+"/");
					//保存本次同步后的时间
					syncDate.setTime(System.currentTimeMillis());
					syncDateRepositoryImpl.save(syncDate);
				//}
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	/**
	 * 当线程停止时，调用该方法
	 */
	public void stopFtp() {
		if(this.remoteFtpClient!=null) {
			try {
				this.remoteFtpClient.logout();
				this.remoteFtpClient.disconnect();
				System.out.println("remoteFtpClient停止成功");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(this.nativeFtpClient!=null) {
			try {
				this.nativeFtpClient.logout();
				this.nativeFtpClient.disconnect();
				System.out.println("nativeFtpClient停止成功");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 文件同步
	 * @param curDate
	 * @param syncDate
	 */
	private void syncDirFiles(SyncDate syncDate,String curRemotePath,String curNativePath) {
		String curDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		Pattern pattern = Pattern.compile("^-?\\d+(\\.\\d+)?$");
		//判断ftp是否有该文件夹，没有则创建,并切换到该目录下
		//ftpFolderDir(curDate);
		//boolean flog=false;
		//SyncDate syncDate=syncDateRepositoryImpl.getInfoByIdAndPath(this.id, this.path);
		try {
			FTPFile[] remoteFiles=this.remoteFtpClient.listFiles();
			if(remoteFiles!=null && remoteFiles.length>0) {		// 日志记录
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date=new Date(remoteFiles[0].getTimestamp().getTimeInMillis());
				log.info("文件名称: "+remoteFiles[0].getName()+" 文件创建时间:"+(remoteFiles[0].getTimestamp().getTimeInMillis()/1000+60)+"  上一次同步时间: "+(syncDate.getTime()/1000)+" 标准时间:"+sdf.format(date));
			}
			for(FTPFile file:remoteFiles) {
				//判断是否是文件夹，是则递归调用
				if(file.isDirectory()) {
					//首先判断是否是日期文件夹
					Matcher isNum = pattern.matcher(file.getName());
					if(isNum.matches()&& !file.getName().equals(curDate)) {		//是日期文件夹但不是当天文件夹
						continue;
					}else {
						curRemotePath+=file.getName()+"/";
						curNativePath+=file.getName()+"/";
						ftpFolderDir(curRemotePath,curNativePath);
						//flog=true;
						syncDirFiles(syncDate,curRemotePath,curNativePath);
					}
					//String str=this.remoteFtpClient.printWorkingDirectory();
					//ftpFolderDir(str.substring(0, str.lastIndexOf('/')));
//					curNativePath=curNativePath.substring(0, curNativePath.lastIndexOf('/'));
//					curNativePath=curNativePath.substring(0, curNativePath.lastIndexOf('/')+1);
//					curRemotePath=curRemotePath.substring(0, curRemotePath.lastIndexOf('/'));
//					curRemotePath=curRemotePath.substring(0, curRemotePath.lastIndexOf('/')+1);
					//返回上一级目录
					curNativePath=interceptString(curNativePath);
				    curRemotePath=interceptString(curRemotePath);
					ftpFolderDir(curRemotePath,curNativePath);
					//System.out.println(this.nativeFtpClient.printWorkingDirectory()+" "+this.remoteFtpClient.printWorkingDirectory());
					continue;
				}
				//返回到上一级目录
//				if(flog) {
//					String str=this.remoteFtpClient.printWorkingDirectory();
//					ftpFolderDir(str.substring(0, str.lastIndexOf('/')));
//					flog=false;
//				}
				System.out.println(file.getName());
				//long fileTime=Long.parseLong(file.getName().substring(0, file.getName().indexOf(".")));
				//ftp文件时间不能精确到秒，故加60秒，以达到都可以同步
				if(!file.isDirectory() && (file.getTimestamp().getTimeInMillis()/1000+60)>=(syncDate.getTime()/1000)) {
				//if(!file.isDirectory() && fileTime>=(syncDate.getTime())) {
					 this.remoteFtpClient.setRemoteVerificationEnabled(false);
					 OutputStream is = this.nativeFtpClient.storeFileStream(file.getName());
					 this.remoteFtpClient.setFileType(FTP.BINARY_FILE_TYPE);
					 // 通过流把remoteFtpClient复制到nativeFtpClient
					 this.remoteFtpClient.retrieveFile(new String(file.getName().getBytes("GBK"),"iso-8859-1"), is);
					 is.close();
					 this.nativeFtpClient.completePendingCommand();
				}
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//截取到上一级目录
	private String interceptString(String curPath) {
		curPath=curPath.substring(0, curPath.lastIndexOf('/'));
		curPath=curPath.substring(0, curPath.lastIndexOf('/')+1);
		return curPath;
	}
}
