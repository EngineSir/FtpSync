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

			try {
				ftpClient.changeWorkingDirectory(path);
				FTPFile[] files = ftpClient.listFiles();
				int pageIndex = (pageNo - 1) * pageSize + 1;
				int pageTotal = pageNo * pageSize;
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
						System.out.println("退出成功");
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
			nativeFtpClient.makeDirectory(path);
			nativeFtpClient.changeWorkingDirectory(path);
			FTPFile[] nativeFileName = nativeFtpClient.listFiles(name);
			// 没有，则同步
			if (nativeFileName == null || nativeFileName.length == 0) {
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
			// 配置同步任务
			if (null != type && !"".equals(type) && "1".equals(type)) {

				// 存储父级文件同步信息
				SyncDate syncFile = new SyncDate();
				syncFile.setId(Integer.parseInt(ftpId));
				syncFile.setPath(remotePath + nativePath);
				syncFile.setTime(0);
				syncDateRepositoryImpl.save(syncFile);
				Timer timer=new Timer();
				ThreadSyncFile task=new ThreadSyncFile(ftpLinkInfo.get(), remotePath, nativePath,
								syncDateRepositoryImpl);
				timer.schedule(task, 0, Long.parseLong(ftpLinkInfo.get().getSyncTime())*1000);
				Application.setTimerMap(ftpId + remotePath + nativePath, timer);
				Application.setThreadSyncFileMap(ftpId +  remotePath + nativePath, task);

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
				Timer timer=new Timer();
				ThreadSyncFile task=new ThreadSyncFile(ftpLinkInfo.get(), remotePath, remotePath,
								syncDateRepositoryImpl);
						
				timer.schedule(task, 0, Long.parseLong(ftpLinkInfo.get().getSyncTime())*1000);
					
				Application.setTimerMap(ftpId + remotePath, timer);
				Application.setThreadSyncFileMap(ftpId + remotePath, task);
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
		Timer timer = Application.getTimerMap(id + path);
		ThreadSyncFile task=Application.getThreadSyncFileMap(id + path);
		if (timer != null) {
			timer.cancel();
			Application.removeTimerMap(id + path);
			syncDateRepositoryImpl.delInfoByIdAndPath(Integer.parseInt(id), path);
		}
		if(task !=null) {
			task.stopFtp();
			Application.removeThreadSyncFileMap(id + path);
		}

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
	public Result stopSyncTask(String ftpId, String nativePath, String remotePath, String id) {

		syncDateRepositoryImpl.delInfoByIdAndPath(Integer.parseInt(ftpId), remotePath+nativePath);
		Timer timer = Application.getTimerMap(ftpId + remotePath+nativePath);
		ThreadSyncFile task=Application.getThreadSyncFileMap(ftpId + remotePath+nativePath);
		if (timer != null) {
			timer.cancel();
			Application.removeTimerMap(ftpId + remotePath+nativePath);
			syncDateRepositoryImpl.delInfoByIdAndPath(Integer.parseInt(ftpId), remotePath+nativePath);
		}
					
		if(task != null) {
			task.stopFtp();
			Application.removeThreadSyncFileMap(ftpId + remotePath+nativePath);
		}
			/**
			 * 重新保存配置同步任务flag
			 */
			Optional<SyncTask> syncTaskOption = syncTaskRepositoryImpl.findById(Integer.parseInt(id));
			SyncTask syncTask = syncTaskOption.get();
			syncTask.setFlag(0);
			syncTaskRepositoryImpl.save(syncTask);

		return new Result(true, "停止同步成功", null);
	}
}
