package com.arms.api.engine.services.dashboard.sankey;

import com.arms.api.engine.services.dashboard.common.ElasticSearchQueryHelper;
import com.arms.api.engine.models.dashboard.sankey.SankeyElasticSearchData;
import com.arms.api.engine.repositories.지라이슈_저장소;

import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색결과_목록_메인;
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
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
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
    public Map<String, List<SankeyElasticSearchData>> 제품_버전별_담당자_목록(Long pdServiceLink, List<Long> pdServiceVersionLinks, int maxResults) throws IOException {
        BoolQueryBuilder boolQuery = es.boolQueryBuilder(pdServiceLink, pdServiceVersionLinks)
                .filter(QueryBuilders.termQuery("isReq", false))
                .filter(QueryBuilders.existsQuery("assignee"));

        TermsAggregationBuilder versionsAgg = AggregationBuilders.terms("versions").field("pdServiceVersion");
        TermsAggregationBuilder assigneesAgg = AggregationBuilders.terms("assignees")
                .field("assignee.assignee_accountId.keyword")
                .order(BucketOrder.count(false));

        if(maxResults > 0) {
            assigneesAgg.size(maxResults);
        }

        assigneesAgg.subAggregation(AggregationBuilders.terms("displayNames").field("assignee.assignee_displayName.keyword"));
        versionsAgg.subAggregation(assigneesAgg);

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        nativeSearchQueryBuilder.withQuery(boolQuery)
                .withAggregations(versionsAgg);

        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource().query(boolQuery).aggregation(versionsAgg);

        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());

        Map<String, List<SankeyElasticSearchData>> versionAssigneesMap = new HashMap<>();


        List<검색결과> versions = 검색결과_목록_메인.get검색결과().get("versions");

        for (검색결과 버전 : versions) {
            String version = 버전.get필드명();

            List<com.arms.elasticsearch.util.검색결과> assignees = 버전.get하위검색결과().get("assignees");

            List<SankeyElasticSearchData> assigneeList = new ArrayList<>();

            for (검색결과 담당자 : assignees) {
                String accountId = 담당자.get필드명();

                List<검색결과> displayNames = 담당자.get하위검색결과().get("displayNames");
                assigneeList.add(new SankeyElasticSearchData(accountId
                        , displayNames.stream()
                        .findFirst()
                        .map(displayName->displayName.get필드명()).orElseGet(()->"N/A")));
            }

            versionAssigneesMap.put(version, assigneeList);
        }

        return versionAssigneesMap;
    }
}
