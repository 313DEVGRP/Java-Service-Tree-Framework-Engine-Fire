package com.arms.api.alm.issue.repository;

import com.arms.api.index_entity.이슈_인덱스;
import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.esquery.EsQueryBuilder;
import com.arms.elasticsearch.query.esquery.esboolquery.must.MustTermQuery;
import com.arms.elasticsearch.query.filter.TermsQueryFilter;
import com.arms.elasticsearch.repository.공통저장소;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface 지라이슈_저장소 extends 공통저장소<이슈_인덱스,String>{

    default List<이슈_인덱스> findByPdServiceIdAndPdServiceVersionsIn(Long pdServiceLink, Long[] pdServiceVersionLinks){
        EsQuery esQuery = new EsQueryBuilder()
            .bool(
                new MustTermQuery("pdServiceId", pdServiceLink),
                new TermsQueryFilter("pdServiceVersions", pdServiceVersionLinks)
            );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
            .withQuery(boolQuery)
            .withMaxResults(10000);

        return this.normalSearch(nativeSearchQueryBuilder.build());
    };

    default List<이슈_인덱스> findByIsReqAndPdServiceIdAndPdServiceVersionsIn(boolean isReq, Long pdServiceLink
        , Long[] pdServiceVersionLinks){
        EsQuery esQuery = new EsQueryBuilder()
            .bool(
                new MustTermQuery("isReq", isReq),
                new MustTermQuery("pdServiceId", pdServiceLink),
                new TermsQueryFilter("pdServiceVersions", pdServiceVersionLinks)
            );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
            .withQuery(boolQuery)
            .withMaxResults(10000);
        return this.normalSearch(nativeSearchQueryBuilder.build());
    };

    default List<이슈_인덱스> findByParentReqKeyIn(List<String> parentReqKeys){
        EsQuery esQuery = new EsQueryBuilder()
            .bool(
                new TermsQueryFilter("parentReqKey", parentReqKeys)
            );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
            .withQuery(boolQuery)
            .withMaxResults(10000);
        return this.normalSearch(nativeSearchQueryBuilder.build());
    };
}
