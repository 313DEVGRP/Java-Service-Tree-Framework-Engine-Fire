package com.arms.elasticsearch.services;

import com.arms.elasticsearch.helper.인덱스자료;
import com.arms.elasticsearch.models.집계_응답;
import com.arms.elasticsearch.repositories.지라이슈_저장소;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("지라이슈_대시보드_서비스")
@AllArgsConstructor
public class 지라이슈_검색엔진_대시보드 implements 지라이슈_대시보드_서비스 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라이슈_저장소 지라이슈저장소;

    @Override
    public List<집계_응답> 이슈상태집계(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("pdServiceId", pdServiceLink));

        if (pdServiceVersionLinks != null && !pdServiceVersionLinks.isEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery("pdServiceVersion", pdServiceVersionLinks));
        }

        TermsAggregationBuilder issueStatusAgg = AggregationBuilders.terms("statuses").field("status.status_name.keyword");

        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource().query(boolQuery).aggregation(issueStatusAgg);

        SearchResponse searchResponse = 지라이슈저장소.search(getSearchRequest(sourceBuilder), RequestOptions.DEFAULT);

        Terms status = searchResponse.getAggregations().get("statuses");

        return status.getBuckets().stream()
                .map(bucket -> {
                    return new 집계_응답(bucket.getKeyAsString(), bucket.getDocCount());
                })
                .collect(Collectors.toList());
    }

    public static SearchRequest getSearchRequest(SearchSourceBuilder sourceBuilder) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(인덱스자료.지라이슈_인덱스명);
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }


}
