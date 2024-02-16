package com.arms.api.serverinfo.repositories;

import com.arms.api.engine.models.지라이슈;
import com.arms.api.serverinfo.model.서버정보_엔티티;
import com.arms.elasticsearch.util.repository.공통저장소;
import org.springframework.stereotype.Repository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@Repository
public interface 서버정보_저장소 extends 공통저장소<서버정보_엔티티,Long>{
}
