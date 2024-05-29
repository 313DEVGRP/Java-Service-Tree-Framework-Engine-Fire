package com.arms.api.alm.issue.base.service;


import com.arms.api.alm.issue.base.model.지라이슈_엔티티;
import com.arms.api.alm.issue.base.repository.지라이슈_저장소;
import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.model.dto.기본_검색_요청;
import com.arms.elasticsearch.query.esquery.EsQueryBuilder;
import com.arms.elasticsearch.query.factory.creator.기본_쿼리_생성기;
import com.arms.elasticsearch.query.filter.TermsQueryFilter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class 서브테스크_조회 {

    private 지라이슈_저장소 지라이슈_저장소;

    public List<지라이슈_엔티티> 요구사항_링크드이슈_서브테스크_검색하기(Long 서버_아이디, String 이슈_키) {

        EsQuery esQuery = new EsQueryBuilder()
            .bool(
                 new TermsQueryFilter("parentReqKey",이슈_키)
                ,new TermsQueryFilter("jira_server_id",서버_아이디)
            );

        return 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(new 기본_검색_요청() {}, esQuery).생성());
    }


}
