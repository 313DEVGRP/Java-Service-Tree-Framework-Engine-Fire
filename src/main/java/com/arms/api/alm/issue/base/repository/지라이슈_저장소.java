package com.arms.api.alm.issue.base.repository;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.egovframework.javaservice.esframework.repository.공통저장소;
import org.springframework.stereotype.Repository;

@Repository
public interface 지라이슈_저장소 extends 공통저장소<지라이슈_엔티티,String>{

}
