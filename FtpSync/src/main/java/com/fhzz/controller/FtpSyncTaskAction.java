package com.fhzz.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fhzz.service.FtpSyncTaskService;
import com.fhzz.tool.Result;

@RestController
@RequestMapping("/ftpSync")
public class FtpSyncTaskAction {

	@Resource
	private FtpSyncTaskService ftpSyncTaskService;
	
	/**
	 * 	根据key和path获取目录该目录下的文件
	 * @param key
	 * @param path
	 * @return
	 */
	@GetMapping("/getRemoteFtpFileDir")
	public Result getRemoteFtpFileDir(@RequestParam("key") String key,@RequestParam("path") String path,@RequestParam(value="pageSize",required =false) String pageSize,@RequestParam(value="pageNo",required =false) String pageNo,@RequestParam(value="search",required =false) String search) {
		return ftpSyncTaskService.getRemoteFtpFileDir(key,path,Integer.parseInt(pageSize),Integer.parseInt(pageNo), search);
	}
	
	/**
	 * 	文件同步
	 * @param id
	 * @param path
	 * @param name
	 * @param type
	 * @return
	 */
	@PostMapping("/syncFiles")
	public Result syncFiles(@RequestParam("id") String id ,@RequestParam("path") String path ,@RequestParam(value="name",required = false) String name ,@RequestParam("type") String type) {
		return ftpSyncTaskService.syncFiles(id,path,name,type);
	}
	
	/**
	 * 	停止同步
	 * @param id
	 * @param path
	 * @return
	 */
	@PostMapping("/syncFoldeStop")
	public Result syncFoldeStop(@RequestParam("id") String id ,@RequestParam("path") String path) {
		return ftpSyncTaskService.syncFoldeStop(id,path);
	}
	
	/**
	 * 	根据文件获取文件内容
	 * @param id
	 * @param path
	 * @param name
	 * @return
	 */
	@GetMapping("/getFileContent")
	public Result getFileContent(@RequestParam("id") Integer id,@RequestParam("path") String path,@RequestParam("name") String name) {
		return ftpSyncTaskService.getFileContent(id,path,name);
	}
	
	/**
	 * 配置任务同步
	 * @param id
	 * @param nativePath
	 * @param remotePath
	 * @param type
	 * @param ftpId
	 * @return
	 */
	@PostMapping("/syncTask")
	public Result syncTask(@RequestParam("id") String id ,@RequestParam("nativePath") String nativePath ,@RequestParam("remotePath") String remotePath ,@RequestParam("type") String type,@RequestParam("ftpId") String ftpId) {
		return ftpSyncTaskService.syncTask(ftpId,nativePath,remotePath,type,id);
	}
	
	/**
	 * 停止配置同步任务
	 * @param id
	 * @param nativePath
	 * @param remotePath
	 * @param type
	 * @param ftpId
	 * @return
	 */
	@PostMapping("/stopSyncTask")
	public Result stopSyncTask(@RequestParam("id") String id ,@RequestParam("nativePath") String nativePath ,@RequestParam("remotePath") String remotePath ,@RequestParam("type") String type,@RequestParam("ftpId") String ftpId) {
		return ftpSyncTaskService.stopSyncTask(ftpId,nativePath,remotePath,type,id);
	}
}
