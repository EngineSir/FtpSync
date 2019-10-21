package com.fhzz.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.fhzz.entity.SyncTask;

public interface SyncTaskRepositoryImpl extends CrudRepository<SyncTask, Integer>, JpaSpecificationExecutor<SyncTask>{
}
