package com.arms.api.serverinfo.repository;

import com.arms.api.index_entity.서버정보_인덱스;
import com.arms.elasticsearch.repository.공통저장소;
import org.springframework.stereotype.Repository;

@Repository
public interface 서버정보_저장소 extends 공통저장소<서버정보_인덱스,Long>{
}
