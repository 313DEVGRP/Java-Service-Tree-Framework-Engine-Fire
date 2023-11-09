package com.arms.elasticsearch.repositories;

import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.util.repository.공통저장소;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface 지라이슈_저장소 extends 공통저장소<지라이슈,String>,ElasticsearchCustom{


    List<지라이슈> findByIsReqAndPdServiceIdAndPdServiceVersionIn(boolean isReq, Long pdServiceLink, List<Long> pdServiceVersionLinks);

    List<지라이슈> findByParentReqKeyIn(List<String> parentReqKeys);
}
