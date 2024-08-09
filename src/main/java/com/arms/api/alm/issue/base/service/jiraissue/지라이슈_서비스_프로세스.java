package com.arms.api.alm.issue.base.service.jiraissue;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.egovframework.javaservice.esframework.EsQuery;
import com.arms.egovframework.javaservice.esframework.esquery.EsQueryBuilder;
import com.arms.egovframework.javaservice.esframework.factory.creator.기본_쿼리_생성기;
import com.arms.egovframework.javaservice.esframework.filter.TermsQueryFilter;
import com.arms.egovframework.javaservice.esframework.model.dto.기본_검색_요청;
import com.arms.egovframework.javaservice.esframework.must.TermQueryMust;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import com.arms.api.alm.issue.base.repository.지라이슈_저장소;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class 지라이슈_서비스_프로세스 implements 지라이슈_서비스 {

    private final 지라이슈_저장소 지라이슈_저장소;

    @Override
    public List<지라이슈_엔티티> 지라이슈_조회(Long pdServiceLink, Long[] pdServiceVersionLinks) {
        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("pdServiceId", pdServiceLink),
                        new TermsQueryFilter("pdServiceVersions", pdServiceVersionLinks)
                );
        기본_검색_요청 기본_검색_요청 = new 기본_검색_요청() {};
        기본_검색_요청.set크기(10000);
        return 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(기본_검색_요청,esQuery).생성());
    }

    @Override
    public List<지라이슈_엔티티> 지라이슈_조회(boolean isReq, Long pdServiceLink, Long[] pdServiceVersionLinks) {
        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("isReq", isReq),
                        new TermQueryMust("pdServiceId", pdServiceLink),
                        new TermsQueryFilter("pdServiceVersions", pdServiceVersionLinks)
                );
        기본_검색_요청 기본_검색_요청 = new 기본_검색_요청() {};
        기본_검색_요청.set크기(10000);
        return 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(기본_검색_요청,esQuery).생성());
    }

    @Override
    public List<지라이슈_엔티티> 지라이슈_조회(List<String> parentReqKeys) {
        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                        new TermsQueryFilter("parentReqKey", parentReqKeys)
                );
        기본_검색_요청 기본_검색_요청 = new 기본_검색_요청() {};
        기본_검색_요청.set크기(10000);
        return 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(기본_검색_요청,esQuery).생성());
    }

    public 지라이슈_엔티티 이슈_조회하기(String 조회조건_아이디){

        EsQuery esQuery = new EsQueryBuilder()
            .bool(
                new TermQueryMust("id", 조회조건_아이디)
            );
        List<지라이슈_엔티티> 검색결과 = 지라이슈_저장소.normalSearch(기본_쿼리_생성기.기본검색(new 기본_검색_요청(){}, esQuery).생성());

        if (검색결과 == null || 검색결과.isEmpty()) {
            return null;
        }

        return 검색결과.stream().findFirst().orElseGet(지라이슈_엔티티::new);
    }

    public SearchHit<지라이슈_엔티티> 이슈상세_조회하기(String 조회조건_아이디){

        EsQuery esQuery = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("id", 조회조건_아이디)
                );
        List<SearchHit<지라이슈_엔티티>> 검색결과 = 지라이슈_저장소.normalSearchHits(기본_쿼리_생성기.기본검색(new 기본_검색_요청() {
        }, esQuery).생성());

        if (검색결과 == null || 검색결과.isEmpty()) {
            return null;
        }

        return 검색결과.get(0);
    }
}
