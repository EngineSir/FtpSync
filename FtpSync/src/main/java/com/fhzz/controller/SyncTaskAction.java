package com.fhzz.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fhzz.service.SyncTaskService;
import com.fhzz.tool.Result;

@RestController
@RequestMapping("/syncTask")
public class SyncTaskAction {

	@Resource
	private SyncTaskService syncTaskService;
	
	/**
	 * 配置同步任务
	 * @param ftpId
	 * @param remotePath
	 * @param nativePath
	 * @param taskName
	 * @return
	 */
	@PostMapping("/addTaskPath")
	public Result addTaskPath(@RequestParam("ftpId") String ftpId,@RequestParam("remotePath") String remotePath,@RequestParam("nativePath") String nativePath,@RequestParam("taskName") String taskName) {
		return syncTaskService.addTaskPath(ftpId,remotePath,nativePath,taskName,null);
	}
	
	/**
	 * 获取同步任务
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@GetMapping("/getSyncTask")
	public Result getSyncTask(@RequestParam(value="pageSize") String pageSize,@RequestParam(value="pageNo") String pageNo,@RequestParam(value="search",required = false) String search) {
		return syncTaskService.getSyncTask(Integer.parseInt(pageSize),Integer.parseInt(pageNo),search);
	}
	
	/**
	 * 更新同步任务
	 * @param id
	 * @param remotePath
	 * @param nativePath
	 * @param taskName
	 * @param ftpId
	 * @return
	 */
	@PostMapping("/updateTaskPath")
	public Result updateTaskPath(@RequestParam("id") String id,@RequestParam("remotePath") String remotePath,@RequestParam("nativePath") String nativePath,@RequestParam("taskName") String taskName,@RequestParam("ftpId") String ftpId) {
		return syncTaskService.addTaskPath(ftpId,remotePath,nativePath,taskName,id);
	}
	
	@DeleteMapping("/delFtpSync")
	public Result delFtpSync(@RequestParam("id") String id) {
		return syncTaskService.delFtpSync(id);
	}
}
