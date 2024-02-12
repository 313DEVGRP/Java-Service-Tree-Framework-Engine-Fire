package com.arms.api.engine.repositories;

import com.arms.api.engine.models.지라이슈;
import com.arms.elasticsearch.util.query.EsQuery;
import com.arms.elasticsearch.util.query.EsQueryBuilder;
import com.arms.elasticsearch.util.query.bool.TermQueryMust;
import com.arms.elasticsearch.util.query.bool.TermsQueryFilter;
import com.arms.elasticsearch.util.repository.공통저장소;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface 지라이슈_저장소 extends 공통저장소<지라이슈,String>{

    default List<지라이슈> findByPdServiceIdAndPdServiceVersionIn(Long pdServiceLink, List<Long> pdServiceVersionLinks){
        EsQuery esQuery = new EsQueryBuilder()
            .bool(
                new TermQueryMust("pdServiceId", pdServiceLink),
                new TermsQueryFilter("pdServiceVersion", pdServiceVersionLinks)
            );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
            .withQuery(boolQuery)
            .withMaxResults(10000);

        return this.normalSearch(nativeSearchQueryBuilder.build());
    };

    default List<지라이슈> findByIsReqAndPdServiceIdAndPdServiceVersionIn(boolean isReq, Long pdServiceLink
        , List<Long> pdServiceVersionLinks){
        EsQuery esQuery = new EsQueryBuilder()
            .bool(
                new TermQueryMust("isReq", isReq),
                new TermQueryMust("pdServiceId", pdServiceLink),
                new TermsQueryFilter("pdServiceVersion", pdServiceVersionLinks)
            );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });

    List<지라이슈> findByIsReqAndPdServiceIdAndPdServiceVersionIn(boolean isReq, Long pdServiceLink, List<Long> pdServiceVersionLinks);

    List<지라이슈> findByParentReqKeyIn(List<String> parentReqKeys);
}
