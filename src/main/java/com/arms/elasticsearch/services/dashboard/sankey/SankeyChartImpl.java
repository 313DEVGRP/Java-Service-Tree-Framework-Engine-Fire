package com.arms.elasticsearch.services.dashboard.sankey;

import com.arms.elasticsearch.models.dashboard.sankey.SankeyElasticSearchData;
import com.arms.elasticsearch.repositories.지라이슈_저장소;
import com.arms.elasticsearch.services.dashboard.common.ElasticSearchQueryHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("지라이슈_대시보드_제품별_버전_및_작업자")
@RequiredArgsConstructor
public class SankeyChartImpl implements SankeyChart {
    private final 지라이슈_저장소 지라이슈저장소;
    private final ElasticSearchQueryHelper es;

    @Override
    public Map<String, List<SankeyElasticSearchData>> 제품_버전별_담당자_목록(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException {
        BoolQueryBuilder boolQuery = es.boolQueryBuilder(pdServiceLink, pdServiceVersionLinks)
                .filter(QueryBuilders.termQuery("isReq", false))
                .filter(QueryBuilders.existsQuery("assignee"));

        TermsAggregationBuilder versionsAgg = AggregationBuilders.terms("versions").field("pdServiceVersion");
        TermsAggregationBuilder assigneesAgg = AggregationBuilders.terms("assignees")
                .field("assignee.assignee_accountId.keyword")
                .order(BucketOrder.count(false))
                .size(3);

        assigneesAgg.subAggregation(AggregationBuilders.terms("displayNames").field("assignee.assignee_displayName.keyword"));
        versionsAgg.subAggregation(assigneesAgg);

        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource().query(boolQuery).aggregation(versionsAgg);

        SearchResponse searchResponse = 지라이슈저장소.search(es.getSearchRequest(sourceBuilder), RequestOptions.DEFAULT);

        Map<String, List<SankeyElasticSearchData>> versionAssigneesMap = new HashMap<>();
        Terms versions = searchResponse.getAggregations().get("versions");

        for (Terms.Bucket versionBucket : versions.getBuckets()) {
            String version = versionBucket.getKeyAsString();

            Terms assignees = versionBucket.getAggregations().get("assignees");

            List<SankeyElasticSearchData> assigneeList = new ArrayList<>();

            for (Terms.Bucket assigneeBucket : assignees.getBuckets()) {
                String accountId = assigneeBucket.getKeyAsString();

                Terms displayNames = assigneeBucket.getAggregations().get("displayNames");
                String displayName = displayNames.getBuckets().get(0).getKeyAsString();

                assigneeList.add(new SankeyElasticSearchData(accountId, displayName));
            }

            versionAssigneesMap.put(version, assigneeList);
        }

        return versionAssigneesMap;
    }
}
