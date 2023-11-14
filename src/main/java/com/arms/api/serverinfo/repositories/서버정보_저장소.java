package com.arms.api.serverinfo.repositories;

import com.arms.api.serverinfo.model.서버정보_엔티티;
import org.springframework.stereotype.Repository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@Repository
public interface 서버정보_저장소 extends ElasticsearchRepository<서버정보_엔티티, Long>{
}
