package com.fhzz.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.util.Strings;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import com.fhzz.entity.FtplinkInfo;
import com.fhzz.entity.SyncDate;
import com.fhzz.entity.param.FtpLinkInfoParam;
import com.fhzz.repository.FtpLinkInfoRepositoryImpl;
import com.fhzz.repository.SyncDateRepositoryImpl;
import com.fhzz.tool.FtpUtil;
import com.fhzz.tool.MD5Util;
import com.fhzz.tool.PageResult;
import com.fhzz.tool.Result;
import com.google.common.collect.Lists;

@Service
public class FtpLinkInfoService {

	@Resource
	private FtpLinkInfoRepositoryImpl ftpLinkInfoRepositoryImpl;

	@PersistenceContext // 注入的是实体管理器,执行持久化操作
	private EntityManager entityManager;
	
	@Resource
	private SyncDateRepositoryImpl syncDateRepositoryImpl;
	
	/**
	 * 	保存或更新ftp连接信息
	 * @param ftpLinkInfoParam
	 * @return
	 */
	public Result saveFtpLink(FtpLinkInfoParam ftpLinkInfoParam,String type)  {
		System.out.println(ftpLinkInfoParam);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		FTPClient remoteFtp=FtpUtil.getFtpClient(ftpLinkInfoParam.getRemote_ip(),ftpLinkInfoParam.getRemote_port(),ftpLinkInfoParam.getRemote_username(),ftpLinkInfoParam.getRemote_password());
		FTPClient nativeFtp=FtpUtil.getFtpClient(ftpLinkInfoParam.getNative_ip(),ftpLinkInfoParam.getNative_port(),ftpLinkInfoParam.getNative_username(),ftpLinkInfoParam.getNative_password());
		try {
			if(remoteFtp.getReplyCode()==230 && nativeFtp.getReplyCode()==230) {
				//type 则进行保存，为0进行测试连接
				if(null!=type && !"".equals(type) && type.equals("1")) {
					FtplinkInfo ftpLinkInfo = new FtplinkInfo();
					if(!"".equals(ftpLinkInfoParam.getId())) {
						ftpLinkInfo.setId(Integer.parseInt(ftpLinkInfoParam.getId()));
					}
					ftpLinkInfo.setTask(ftpLinkInfoParam.getTask());
					ftpLinkInfo.setNativeIp(ftpLinkInfoParam.getNative_ip());
					ftpLinkInfo.setNativePassword(ftpLinkInfoParam.getNative_password());
					if(null==ftpLinkInfoParam.getSync_time() || "".equals(ftpLinkInfoParam.getSync_time())) {
						ftpLinkInfo.setSyncTime("600");
					}else {
						ftpLinkInfo.setSyncTime(ftpLinkInfoParam.getSync_time());
					}
					ftpLinkInfo.setRemoteUsername(ftpLinkInfoParam.getRemote_username());
					ftpLinkInfo.setRemotePort(ftpLinkInfoParam.getRemote_port());
					ftpLinkInfo.setTime(sdf.format(new Date()));
					ftpLinkInfo.setRemotePassword(ftpLinkInfoParam.getRemote_password());
					ftpLinkInfo.setRemoteIp(ftpLinkInfoParam.getRemote_ip());
					ftpLinkInfo.setNativePort(ftpLinkInfoParam.getNative_port());
					ftpLinkInfo.setCreater(ftpLinkInfoParam.getCreater());
					ftpLinkInfo.setNativeUsername(ftpLinkInfoParam.getNative_username());
					ftpLinkInfoRepositoryImpl.save(ftpLinkInfo);
				}
				return new Result(true, "测试连接成功", null);
			}else if(remoteFtp.getReplyCode()!=230 && nativeFtp.getReplyCode()!=230) {
				return new Result(false, "远程ftp和本地ftp连接失败", null);
			}else if(remoteFtp.getReplyCode()!=230) {
				return new Result(false, "远程ftp连接失败", null);
			}else if(nativeFtp.getReplyCode()!=230) {
				return new Result(false, "本地ftp连接失败", null);
			}
		}catch(Exception e) {
			
		}finally {
			if(remoteFtp!=null) {
				try {
					remoteFtp.logout();
					remoteFtp.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(nativeFtp!=null) {
				try {
					nativeFtp.logout();
					nativeFtp.disconnect();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return new Result(true, "未知连接错误", null);
	}
	
	/**
	 * 	获取ftp连接信息
	 * @param pageSize
	 * @param pageNo
	 * @return
	 */
	public Result getFtpLink(String pageSize, String pageNo,String search) {
		List<FtplinkInfo> list = null;
		if (Strings.isEmpty(pageSize) && Strings.isEmpty(pageNo)) {
			list = Lists.newArrayList(ftpLinkInfoRepositoryImpl.findAll());
			return new Result(true, "查询成功", list);
		} else {
			String sql = "select * from ftplink_info";
			String likeSql="";
			if(null!=search && !"".equals(search)) {
				likeSql=" where task like '%"+search+"%' or creater like '%"+search+"%' or remote_ip like '%"+search+"%' or native_ip like '%"+search+"%' ";
				
			}
			
			String limit=" limit "+(Integer.parseInt(pageNo)-1)*Integer.parseInt(pageSize)+","+Integer.parseInt(pageSize);
			Query countQuery = this.entityManager.createNativeQuery("select count(*) from ftplink_info"+likeSql);
			int count = Integer.valueOf(countQuery.getResultList().get(0).toString());
			
			Session session = entityManager.unwrap(org.hibernate.Session.class);
			NativeQuery<?> query = session.createNativeQuery(sql+likeSql+limit);
			query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
			entityManager.close();
			List<?> rows = query.getResultList();
			return new Result(true, "查询成功", new PageResult(rows,count));
		}

	}
	
	/**
	 * 	删除ftp连接
	 * @param id
	 * @return
	 */
	public Result delFtpLink(String id) {
		List<SyncDate> syncTask=syncDateRepositoryImpl.getSyncTaskById(Integer.parseInt(id));
		if(syncTask.isEmpty() || syncTask.size()==0) {
			ftpLinkInfoRepositoryImpl.deleteById(Integer.parseInt(id));
			return new Result(true,"删除成功","");
		}else {
			return new Result(false,"删除失败，请先停止同步任务","");
		}
	}
	
	/**
	 * 	登录逻辑，目前只有一个账号
	 * @param username
	 * @param pass
	 * @return
	 */
	public Result login(String username,String pass,HttpServletRequest request) {
		if(username.equals("admin") && MD5Util.encrypt(pass).equals(MD5Util.encrypt("123456"))) {
			request.getSession().setAttribute("username", username);
			return new Result(true,"登录成功",null);
		}else {
			return new Result(false,"登录失败",null);
		}
	}
	
	/**
	 * 	退出登录
	 * @param request
	 * @param response
	 */
	public void logout(HttpServletRequest request,ServletResponse response) {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		req.getSession().removeAttribute("username");
		try {
			res.sendRedirect("/login");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
