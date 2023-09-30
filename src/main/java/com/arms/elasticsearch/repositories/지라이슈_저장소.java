package com.arms.elasticsearch.repositories;

import com.arms.elasticsearch.models.지라이슈;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface 지라이슈_저장소 extends ElasticsearchRepository<지라이슈,String>,ElasticsearchCustom {


}
