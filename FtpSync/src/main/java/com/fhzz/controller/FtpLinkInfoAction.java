package com.fhzz.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fhzz.entity.param.FtpLinkInfoParam;
import com.fhzz.service.FtpLinkInfoService;
import com.fhzz.tool.JsonUtils;
import com.fhzz.tool.Result;

@RestController
@RequestMapping("/ftpLink")
public class FtpLinkInfoAction {

	@Resource
	private FtpLinkInfoService ftpLinkInfoService;
	
	/**
	 * 保存ftp链接
	 * @param ftpLinkInfoParam
	 * @return
	 * @throws IOException
	 */
	//FtpLinkInfoParam ftpLinkInfoParam
	@PostMapping("/save")
	public Result saveFtpLink(FtpLinkInfoParam ftpLinkInfoParam,@RequestParam(value="type") String type) throws IOException {
		return ftpLinkInfoService.saveFtpLink(ftpLinkInfoParam,type);
	}
	
	/**
	 * 获取ftp链接列表
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	@GetMapping("/getFtpLink")
	public Result getFtpLink(@RequestParam(value="pageSize",required =false) String pageSize,@RequestParam(value="pageNo",required =false) String pageNo,@RequestParam(value="search",required =false) String search) {
		return ftpLinkInfoService.getFtpLink(pageSize,pageNo,search);
	}
	
	/**
	 * 删除指定ftp连接
	 * @param id
	 * @return
	 */
	@DeleteMapping("/delFtpLink")
	public Result delFtpLink(@RequestParam("id") String id) {
		return ftpLinkInfoService.delFtpLink(id);
	}
	
	/**
	 * admin登录管理
	 * @param username
	 * @param pass
	 * @return
	 */
	@PostMapping("/login")
	public Result login(@RequestParam("username") String username,@RequestParam("pass") String pass,HttpServletRequest request) {
		return ftpLinkInfoService.login(username,pass,request);
	}
	
	/**
	 * 退出登录
	 */
	@GetMapping("/logout")
	public void logout(HttpServletRequest request,ServletResponse response) {
		 ftpLinkInfoService.logout(request,response);
	}
}
