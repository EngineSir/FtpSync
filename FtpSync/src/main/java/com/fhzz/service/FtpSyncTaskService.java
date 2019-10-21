package com.fhzz.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Timer;

import javax.annotation.Resource;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fhzz.entity.FtpFileInfo;
import com.fhzz.entity.FtplinkInfo;
import com.fhzz.entity.SyncDate;
import com.fhzz.entity.SyncTask;
import com.fhzz.repository.FtpLinkInfoRepositoryImpl;
import com.fhzz.repository.SyncDateRepositoryImpl;
import com.fhzz.repository.SyncTaskRepositoryImpl;
import com.fhzz.tool.Application;
import com.fhzz.tool.FtpUtil;
import com.fhzz.tool.PageResult;
import com.fhzz.tool.Result;
import com.fhzz.tool.ThreadSyncFile;

@Service
public class FtpSyncTaskService {

	@Resource
	private FtpLinkInfoRepositoryImpl ftpLinkInfoRepositoryImpl;

	@Resource
	private SyncDateRepositoryImpl syncDateRepositoryImpl;

	@Resource
	SyncTaskRepositoryImpl syncTaskRepositoryImpl;

	/**
	 * 获取远程ftp文件目录
	 * 
	 * @param key
	 * @param path
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public Result getRemoteFtpFileDir(String key, String path, int pageSize, int pageNo,String search) {
		PageResult pageResult = new PageResult();
		if (null != key && !"".equals(key)) {
			Optional<FtplinkInfo> ftpLinkInfo = ftpLinkInfoRepositoryImpl.findById(Integer.parseInt(key));
			FTPClient ftpClient = FtpUtil.getFtpClient(ftpLinkInfo.get().getRemoteIp(),
					ftpLinkInfo.get().getRemotePort(), ftpLinkInfo.get().getRemoteUsername(),
					ftpLinkInfo.get().getRemotePassword());
			ftpClient.setControlEncoding("GBK");
			//List<FtpFileInfo> listFtpInfo = new ArrayList<FtpFileInfo>();

			try {
				ftpClient.changeWorkingDirectory(path);
				FTPFile[] files = ftpClient.listFiles();
				//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				int pageIndex = (pageNo - 1) * pageSize + 1;
				int pageTotal = pageNo * pageSize;
				//int index = 1;
				//search为空说明没进行搜索查询
				if(null==search || "".equals(search)) {
					pageResult=getListFtpInfoByPage(files,pageIndex,pageTotal,key,path);
				}else {	//带搜索参数
					List<FTPFile> listFile=new ArrayList<FTPFile>();
					for(FTPFile f:files) {
						if(f.getName().contains(search)) {
							listFile.add(f);
						}
					}
					FTPFile[] file=new FTPFile[listFile.size()];
					listFile.toArray(file);
					pageResult=getListFtpInfoByPage(file,pageIndex,pageTotal,key,path);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (ftpClient != null) {
					try {
						ftpClient.logout();
						ftpClient.disconnect();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		return new Result(true, "获取列表成功", pageResult);
	}

	/**
	 *	 根据参数获取搜索分页结果
	 * @param files
	 * @param pageIndex
	 * @param pageTotal
	 * @param key
	 * @param path
	 * @return
	 */
	private PageResult getListFtpInfoByPage(FTPFile[] files,int pageIndex,int pageTotal,String key,String path){
		int index=1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		PageResult pageResult = new PageResult();
		List<FtpFileInfo> listFtpInfo = new ArrayList<FtpFileInfo>();
		for (FTPFile f : files) {
			// 根据分页参数进行逻辑分页
			if (index >= pageIndex && index <= pageTotal) {
				long offsetTime=f.getTimestamp().getTimeZone().getOffset(0);
				FtpFileInfo ftpFileInfo = new FtpFileInfo();
				ftpFileInfo.setName(f.getName());
				ftpFileInfo.setType(f.getType() + "");
				ftpFileInfo.setSize(Math.ceil(f.getSize() / 1000.0) + "KB");
				Date date=new Date(f.getTimestamp().getTime().getTime()+offsetTime);
				ftpFileInfo.setUpdateTime(sdf.format(date));
				// 如果是文件夹，则判断是否已经在同步中
				if (f.getType() == 1) {
					SyncDate syncDate = syncDateRepositoryImpl.getInfoByIdAndPath(Integer.parseInt(key),
							path + f.getName());
					if (syncDate != null) { // 同步中
						ftpFileInfo.setSyncFlag(1);
					} else { // 未同步
						ftpFileInfo.setSyncFlag(0);
					}
				}
				listFtpInfo.add(ftpFileInfo);
			}
			if (index > pageTotal) {
				break;
			}
			index++;
		}
		pageResult.setTotal(files.length);
		pageResult.setData(listFtpInfo);
		return pageResult;
	}
	
	
	/**
	 * 文件同步
	 * 
	 * @param id
	 * @param path
	 * @param name
	 * @param type
	 * @return
	 */
	public Result syncFiles(String id, String path, String name, String type) {
		// 单个文件同步
		if ("0".equals(type)) {
			return syncSingleFile(id, path, name);
		} else {
			// 单个文件夹同步
			return syncSingleFolder(id, path + name, path + name, "0", null);
		}
	}

	/**
	 * 单个文件同步
	 * 
	 * @param id
	 * @param path
	 * @param name
	 * @return
	 */
	private Result syncSingleFile(String id, String path, String name) {
		boolean flog = false;
		Optional<FtplinkInfo> ftpLinkInfo = ftpLinkInfoRepositoryImpl.findById(Integer.parseInt(id));
		FTPClient remoteFtpClient = FtpUtil.getFtpClient(ftpLinkInfo.get().getRemoteIp(),
				ftpLinkInfo.get().getRemotePort(), ftpLinkInfo.get().getRemoteUsername(),
				ftpLinkInfo.get().getRemotePassword());
		FTPClient nativeFtpClient = FtpUtil.getFtpClient(ftpLinkInfo.get().getNativeIp(),
				ftpLinkInfo.get().getNativePort(), ftpLinkInfo.get().getNativeUsername(),
				ftpLinkInfo.get().getNativePassword());
		try {
			remoteFtpClient.changeWorkingDirectory(path);
			nativeFtpClient.changeWorkingDirectory(path);
			FTPFile[] nativeFileName = nativeFtpClient.listFiles(name);
			// 没有，则同步
			if (nativeFileName == null || nativeFileName.length == 0) {
				// FTPFile[] ftpListFile = remoteFtpClient.listFiles(name);
				// for(FTPFile f:ftpListFile) {
				remoteFtpClient.setRemoteVerificationEnabled(false);
				OutputStream is = nativeFtpClient.storeFileStream(name);

				remoteFtpClient.setFileType(FTP.BINARY_FILE_TYPE);
				// 通过流把remoteFtpClient复制到nativeFtpClient
				remoteFtpClient.retrieveFile(name, is);
				is.close();
				flog = nativeFtpClient.completePendingCommand();
				// }
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (remoteFtpClient != null) {
				try {
					remoteFtpClient.logout();
					remoteFtpClient.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			if (nativeFtpClient != null) {
				try {
					nativeFtpClient.logout();
					nativeFtpClient.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return new Result(flog, "该文件同步成功", null);
	}

	/**
	 * 单个文件夹同步
	 * 
	 * @param id
	 * @param path
	 * @return
	 */
	private Result syncSingleFolder(String ftpId, String remotePath, String nativePath, String type, String id) {
		Optional<FtplinkInfo> ftpLinkInfo = ftpLinkInfoRepositoryImpl.findById(Integer.parseInt(ftpId));
		FTPClient remoteFtpClient = FtpUtil.getFtpClient(ftpLinkInfo.get().getRemoteIp(),
				ftpLinkInfo.get().getRemotePort(), ftpLinkInfo.get().getRemoteUsername(),
				ftpLinkInfo.get().getRemotePassword());
		FTPClient nativeFtpClient = FtpUtil.getFtpClient(ftpLinkInfo.get().getNativeIp(),
				ftpLinkInfo.get().getNativePort(), ftpLinkInfo.get().getNativeUsername(),
				ftpLinkInfo.get().getNativePassword());
		try {
			 remoteFtpClient.changeWorkingDirectory(remotePath);
			 nativeFtpClient.changeWorkingDirectory(nativePath);
			FTPFile[] remoteFiles = remoteFtpClient.listFiles();
			// 配置同步任务
			if (null != type && !"".equals(type) && "1".equals(type)) {

				// 存储父级文件同步信息
				SyncDate syncFile = new SyncDate();
				syncFile.setId(Integer.parseInt(ftpId));
				syncFile.setPath(remotePath + nativePath);
				syncFile.setTime(0);
				syncDateRepositoryImpl.save(syncFile);
				for (FTPFile file : remoteFiles) {
					if (file.isDirectory()) {
						nativeFtpClient.makeDirectory(file.getName());
						String newRemotePath = remotePath + "/" + file.getName();
						String newNativePath = nativePath + "/" + file.getName();
						SyncDate syncDate = new SyncDate();
						syncDate.setId(Integer.parseInt(ftpId));
						syncDate.setPath(newRemotePath + newNativePath);
						syncDate.setTime(0);
						syncDateRepositoryImpl.save(syncDate);
						Timer timer=new Timer();
						ThreadSyncFile task=new ThreadSyncFile(ftpLinkInfo.get(), newRemotePath, newNativePath,
								syncDateRepositoryImpl);
						timer.schedule(task, 0, Long.parseLong(ftpLinkInfo.get().getSyncTime())*1000);
//						ThreadSyncFile thread = new ThreadSyncFile(ftpLinkInfo.get(), newRemotePath, newNativePath,
//								syncDateRepositoryImpl);
						Application.setTimerMap(ftpId + newRemotePath + newNativePath, timer);
						Application.setThreadSyncFileMap(ftpId +  newRemotePath + newNativePath, task);
						//thread.start();
					}
				}

				/**
				 * 重新保存配置同步任务flag
				 */
				Optional<SyncTask> syncTaskOption = syncTaskRepositoryImpl.findById(Integer.parseInt(id));
				SyncTask syncTask = syncTaskOption.get();
				syncTask.setFlag(1);
				syncTaskRepositoryImpl.save(syncTask);
			} else {
				// 存储父级文件同步信息
				SyncDate syncFile = new SyncDate();
				syncFile.setId(Integer.parseInt(ftpId));
				syncFile.setPath(remotePath);
				syncFile.setTime(0);
				syncDateRepositoryImpl.save(syncFile);
				for (FTPFile file : remoteFiles) {
					if (file.isDirectory()) {
						String newRemotePath = remotePath + "/" + file.getName();
						SyncDate syncDate = new SyncDate();
						syncDate.setId(Integer.parseInt(ftpId));
						syncDate.setPath(newRemotePath);
						syncDate.setTime(0);
						syncDateRepositoryImpl.save(syncDate);
						Timer timer=new Timer();
						ThreadSyncFile task=new ThreadSyncFile(ftpLinkInfo.get(), newRemotePath, newRemotePath,
								syncDateRepositoryImpl);
						
						timer.schedule(task, 0, Long.parseLong(ftpLinkInfo.get().getSyncTime())*1000);
//						ThreadSyncFile thread = new ThreadSyncFile(ftpLinkInfo.get(), newRemotePath, newRemotePath,
//								syncDateRepositoryImpl);
					
					Application.setTimerMap(ftpId + newRemotePath, timer);
					Application.setThreadSyncFileMap(ftpId + newRemotePath, task);
//						thread.start();
					} else {
						syncSingleFile(ftpId, remotePath, file.getName());
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != remoteFtpClient) {
				try {
					remoteFtpClient.logout();
					remoteFtpClient.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
//		ThreadSyncFile thread =new ThreadSyncFile(ftpLinkInfo.get(),path,syncDateRepositoryImpl);
//		Application.setThreadMap(id+path, thread);
//		thread.start();

		return new Result(true, "同步文件夹成功", null);
	}

	/**
	 * 同步停止
	 * 
	 * @param id
	 * @param path
	 * @return
	 */
	public Result syncFoldeStop(String id, String path) {
		syncDateRepositoryImpl.delInfoByIdAndPath(Integer.parseInt(id), path);
		Optional<FtplinkInfo> ftpLinkInfo = ftpLinkInfoRepositoryImpl.findById(Integer.parseInt(id));

		FTPClient remoteFtpClient = FtpUtil.getFtpClient(ftpLinkInfo.get().getRemoteIp(),
				ftpLinkInfo.get().getRemotePort(), ftpLinkInfo.get().getRemoteUsername(),
				ftpLinkInfo.get().getRemotePassword());
		try {
			remoteFtpClient.changeWorkingDirectory(path);
			FTPFile[] remoteFiles = remoteFtpClient.listFiles();
			for (FTPFile file : remoteFiles) {
				if (file.isDirectory()) {
					String newPath = path + "/" + file.getName();
					Timer timer = Application.getTimerMap(id + newPath);
					ThreadSyncFile task=Application.getThreadSyncFileMap(id + newPath);
					//ThreadSyncFile tsf= new 
					//ThreadSyncFile tt=new ThreadSyncFile();
					//timer.
					if (timer != null) {
						timer.cancel();
//						thread.interrupt();
//						thread.setSyncStopFlag(true);
					//	timer.stopFtp();
						Application.removeTimerMap(id + newPath);
						syncDateRepositoryImpl.delInfoByIdAndPath(Integer.parseInt(id), newPath);
					}
					if(task !=null) {
						task.stopFtp();
						Application.removeThreadSyncFileMap(id + newPath);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != remoteFtpClient) {
				try {
					remoteFtpClient.logout();
					remoteFtpClient.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

//		ThreadSyncFile thread=Application.getThreadMap(id+path);
//		if(thread!=null) {
//			thread.interrupt();
//			thread.setSyncStopFlag(true);
//			thread.stopFtp();
//			Application.removeThreadMap(id+path);
//			syncDateRepositoryImpl.delInfoByIdAndPath(Integer.parseInt(id), path);
//		}
		return new Result(true, "停止同步成功", null);
	}

	/**
	 * 获取json文件内容
	 * 
	 * @param id
	 * @param path
	 * @param name
	 * @return
	 */
	public Result getFileContent(Integer id, String path, String name) {
		StringBuffer jsonObj = new StringBuffer();
		Reader reader = null;
		// jsonObj.append("'");
		Optional<FtplinkInfo> ftpLinkInfo = ftpLinkInfoRepositoryImpl.findById(id);
		FTPClient remoteFtpClient = FtpUtil.getFtpClient(ftpLinkInfo.get().getRemoteIp(),
				ftpLinkInfo.get().getRemotePort(), ftpLinkInfo.get().getRemoteUsername(),
				ftpLinkInfo.get().getRemotePassword());
		try {
			remoteFtpClient.changeWorkingDirectory(path);
			// FTPFile[] remoteFileName=remoteFtpClient.listFiles(name);
			// if(null==remoteFileName || remoteFileName.length==0) {
			// return new Result(false,"没有该文件",null);
			// }else {
			// for(FTPFile f:remoteFileName) {
			// if(f.getName().equals(name)) {
			InputStream is = remoteFtpClient.retrieveFileStream(name);
			if (null == is) {
				return new Result(false, "没有该文件", null);
			} else {
				reader = new InputStreamReader(is, "UTF-8");
				if (null != reader) {
					jsonObj.append("[");
					int tempchar;
					while ((tempchar = reader.read()) != -1) {
						if (((char) tempchar) != '\r') {
							jsonObj.append((char) tempchar);
						}
					}
				}

				is.close();
				reader.close();
				// 结束事务(有流返回时需手动调用,否则会报输入流为空)
				remoteFtpClient.completePendingCommand();
			}

			// }
			// }
			// }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != remoteFtpClient) {
				try {
					remoteFtpClient.logout();
					remoteFtpClient.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		jsonObj.append("]");
		// 指定转化成的反序列化类型
		List<Object> obj = JSON.parseObject(jsonObj.toString(), new TypeReference<List<Object>>() {
		});
		return new Result(true, "有该文件", obj);
	}

	/**
	 * 配置任务同步启动
	 * 
	 * @param id
	 * @param nativePath
	 * @param remotePath
	 * @param type
	 * @return
	 */
	public Result syncTask(String ftpId, String nativePath, String remotePath, String type, String id) {

		return syncSingleFolder(ftpId, remotePath, nativePath, type, id);
	}

	/**
	 * 停止配置同步任务
	 * @param ftpId
	 * @param nativePath
	 * @param remotePath
	 * @param type
	 * @param id
	 * @return
	 */
	public Result stopSyncTask(String ftpId, String nativePath, String remotePath, String type, String id) {

		syncDateRepositoryImpl.delInfoByIdAndPath(Integer.parseInt(ftpId), remotePath+nativePath);
		Optional<FtplinkInfo> ftpLinkInfo = ftpLinkInfoRepositoryImpl.findById(Integer.parseInt(ftpId));

		FTPClient remoteFtpClient = FtpUtil.getFtpClient(ftpLinkInfo.get().getRemoteIp(),
				ftpLinkInfo.get().getRemotePort(), ftpLinkInfo.get().getRemoteUsername(),
				ftpLinkInfo.get().getRemotePassword());
		try {
			remoteFtpClient.changeWorkingDirectory(remotePath);
			FTPFile[] remoteFiles = remoteFtpClient.listFiles();
			for (FTPFile file : remoteFiles) {
				if (file.isDirectory()) {
					String newRemotePath = remotePath + "/" + file.getName();
					String newNativePath = nativePath + "/" + file.getName();
					Timer timer = Application.getTimerMap(ftpId + newRemotePath+newNativePath);
					ThreadSyncFile task=Application.getThreadSyncFileMap(ftpId + newRemotePath+newNativePath);
					if (timer != null) {
						timer.cancel();
//						thread.interrupt();
//						thread.setSyncStopFlag(true);
//						thread.stopFtp();
						Application.removeTimerMap(ftpId + newRemotePath+newNativePath);
						syncDateRepositoryImpl.delInfoByIdAndPath(Integer.parseInt(ftpId), newRemotePath+newNativePath);
					}
					
					if(task != null) {
						task.stopFtp();
						Application.removeThreadSyncFileMap(ftpId + newRemotePath+newNativePath);
					}
				}
			}
			/**
			 * 重新保存配置同步任务flag
			 */
			Optional<SyncTask> syncTaskOption = syncTaskRepositoryImpl.findById(Integer.parseInt(id));
			SyncTask syncTask = syncTaskOption.get();
			syncTask.setFlag(0);
			syncTaskRepositoryImpl.save(syncTask);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != remoteFtpClient) {
				try {
					remoteFtpClient.logout();
					remoteFtpClient.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

//		ThreadSyncFile thread=Application.getThreadMap(id+path);
//		if(thread!=null) {
//			thread.interrupt();
//			thread.setSyncStopFlag(true);
//			thread.stopFtp();
//			Application.removeThreadMap(id+path);
//			syncDateRepositoryImpl.delInfoByIdAndPath(Integer.parseInt(id), path);
//		}
		return new Result(true, "停止同步成功", null);
	}
}
