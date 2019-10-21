package com.fhzz.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.fhzz.entity.SyncDate;

public interface SyncDateRepositoryImpl extends CrudRepository<SyncDate, Integer>, JpaSpecificationExecutor<SyncDate>{
	
	/**
	 * 	获取正在同步的同步信息（时间）
	 * @param id
	 * @param path
	 * @return
	 */
	@Query(value = "SELECT * FROM sync_date where id=:id and path=:path", nativeQuery = true)
	public SyncDate getInfoByIdAndPath(@Param("id") Integer id,@Param("path") String path);
	
	/**
	 * 获取该ftp连接的所有同步任务
	 * @param id
	 * @return
	 */
	@Query(value = "SELECT * FROM sync_date where id=:id", nativeQuery = true)
	public List<SyncDate> getSyncTaskById(@Param("id") Integer id);
	
	/**
	 * 删除指定同步任务
	 * @param id
	 * @param path
	 */
	@Transactional
	@Modifying
	@Query(value = "delete FROM sync_date where id=:id and path=:path", nativeQuery = true)
	public void delInfoByIdAndPath(@Param("id") Integer id,@Param("path") String path);
}
