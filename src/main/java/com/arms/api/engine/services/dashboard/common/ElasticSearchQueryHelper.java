package com.arms.api.engine.services.dashboard.common;

import com.arms.elasticsearch.helper.인덱스자료;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ElasticSearchQueryHelper {

    public SearchRequest getSearchRequest(SearchSourceBuilder sourceBuilder) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(인덱스자료.지라이슈_인덱스명);
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }

    public BoolQueryBuilder boolQueryBuilder(Long pdServiceLink, List<Long> pdServiceVersionLinks) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("pdServiceId", pdServiceLink));
        if (pdServiceVersionLinks != null && !pdServiceVersionLinks.isEmpty()) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("pdServiceVersion", pdServiceVersionLinks));
        }
        return boolQueryBuilder;
    }

}
