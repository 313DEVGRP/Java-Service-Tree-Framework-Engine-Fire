package com.arms.api.engine.repositories;

import com.arms.api.engine.models.지라이슈;
import com.arms.elasticsearch.util.repository.공통저장소;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface 지라이슈_저장소 extends 공통저장소<지라이슈,String>{

    List<지라이슈> findByPdServiceIdAndPdServiceVersionsIn(Long pdServiceLink, Long[] pdServiceVersionLinks); //ARMS-277

    List<지라이슈> findByIsReqAndPdServiceIdAndPdServiceVersionsIn(boolean isReq, Long pdServiceLink, Long[] pdServiceVersionLinks); //ARMS-277

    List<지라이슈> findByParentReqKeyIn(List<String> parentReqKeys);
}
