package com.fhzz.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Service;

import com.fhzz.entity.SyncTask;
import com.fhzz.repository.SyncTaskRepositoryImpl;
import com.fhzz.tool.PageResult;
import com.fhzz.tool.Result;

@Service
public class SyncTaskService {
	
	@Resource
	private SyncTaskRepositoryImpl syncTaskRepositoryImpl;
	
	@PersistenceContext // 注入的是实体管理器,执行持久化操作
	private EntityManager entityManager;
	
	/**
	 * 配置同步任务路径
	 * @param ftpId
	 * @param remotePath
	 * @param nativePath
	 * @param taskName
	 * @return
	 */
	public Result addTaskPath(String ftpId,String remotePath,String nativePath,String taskName,String id) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SyncTask syncTask=new SyncTask();
		if(null!=id && !"".equals(id)) {
			syncTask.setId(Integer.parseInt(id));
		}
		syncTask.setFtpId(ftpId);
		syncTask.setNativePath(nativePath);
		syncTask.setTaskName(taskName);
		syncTask.setRemotePath(remotePath);
		syncTask.setTime(sdf.format(new Date()));
		syncTask.setFlag(0);
		syncTaskRepositoryImpl.save(syncTask);
		return new Result(true,"配置同步任务成功",null);
	}
	
	public Result getSyncTask(Integer pageSize,Integer pageNo,String search) {
		String sql = "select * from sync_task";
		String likeSql="";
		if(null!=search && !"".equals(search)){
			likeSql+=" where remote_path like '%"+search+"%' or native_path like '%"+search+"%' or task_name like '%"+search+"%'";
		}
		String limit=" limit "+(pageNo-1)*pageSize+","+pageSize;
		Query countQuery = this.entityManager.createNativeQuery("select count(*) from sync_task"+likeSql);
		int count = Integer.valueOf(countQuery.getResultList().get(0).toString());
		
		Session session = entityManager.unwrap(org.hibernate.Session.class);
		NativeQuery<?> query = session.createNativeQuery(sql+likeSql+limit);
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		entityManager.close();
		List<?> rows = query.getResultList();
		return new Result(true, "查询成功", new PageResult(rows,count));
	}
	public Result delFtpSync(String id) {
		Optional<SyncTask> syncTaskOption=syncTaskRepositoryImpl.findById(Integer.parseInt(id));
		SyncTask syncTask=syncTaskOption.get();
		if(syncTask.getFlag()==1) {
			return new Result(false,"删除失败，请先停止同步任务","");
		}else {
			syncTaskRepositoryImpl.deleteById(Integer.parseInt(id));
			return new Result(true,"删除成功","");
		}
	}
	
}
