package com.arms.api.engine.services.dashboard.donut;

import com.arms.api.engine.models.dashboard.donut.집계_응답;
import com.arms.api.engine.repositories.지라이슈_저장소;
import com.arms.api.engine.services.dashboard.common.ElasticSearchQueryHelper;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색결과_목록_메인;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("지라이슈_대시보드_제품의_이슈_상태_현황")
@RequiredArgsConstructor
public class DonutChartImpl implements DonutChart {
    private final ElasticSearchQueryHelper es;
    private final 지라이슈_저장소 지라이슈저장소;
    @Override
    public List<집계_응답> 이슈상태집계(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException {
        BoolQueryBuilder boolQuery = es.boolQueryBuilder(pdServiceLink, pdServiceVersionLinks);

        TermsAggregationBuilder issueStatusAgg = AggregationBuilders.terms("statuses").field("status.status_name.keyword");

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withAggregations(issueStatusAgg);

        검색결과_목록_메인 검색결과_목록_메인 = new 검색결과_목록_메인(지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build()));

        List<검색결과> statuses = 검색결과_목록_메인.get검색결과().get("statuses");

        return statuses.stream()
                .map(bucket -> new 집계_응답(bucket.get필드명(), bucket.get개수()))
                .collect(Collectors.toList());
    }


}
