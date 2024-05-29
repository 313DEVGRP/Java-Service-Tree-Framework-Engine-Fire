package com.arms.api.alm.serverinfo.repository;

import com.arms.api.alm.serverinfo.model.서버정보_엔티티;
import com.arms.egovframework.javaservice.esframework.repository.공통저장소;
import org.springframework.stereotype.Repository;

@Repository
public interface 서버정보_저장소 extends 공통저장소<서버정보_엔티티,Long>{
}
