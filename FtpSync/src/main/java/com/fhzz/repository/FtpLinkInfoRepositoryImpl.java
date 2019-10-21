package com.fhzz.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.fhzz.entity.FtplinkInfo;

public interface FtpLinkInfoRepositoryImpl extends CrudRepository<FtplinkInfo, Integer>, JpaSpecificationExecutor<FtplinkInfo>, PagingAndSortingRepository<FtplinkInfo, Integer>{
}
