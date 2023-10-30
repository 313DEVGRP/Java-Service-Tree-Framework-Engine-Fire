package com.arms.elasticsearch.services.dashboard.bar;

import com.arms.elasticsearch.models.dashboard.bar.요구사항_지라이슈상태_주별_집계;
import com.arms.elasticsearch.repositories.지라이슈_저장소;
import com.arms.elasticsearch.services.dashboard.common.ElasticSearchQueryHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("지라이슈_대시보드_주_별_제품의_요구사항_및_이슈상태_누적_현황")
@RequiredArgsConstructor
public class BarChartImpl implements BarChart {

    private final 지라이슈_저장소 지라이슈저장소;
    private final ElasticSearchQueryHelper es;


    public 요구사항_지라이슈상태_주별_집계 누적데이터조회(Long pdServiceLink, List<Long> pdServiceVersionLinks, LocalDate monthAgo) throws IOException {
        // 총 이슈 개수를 구하기 위한 쿼리
        BoolQueryBuilder boolQueryForTotalIssues = es.boolQueryBuilder(pdServiceLink, pdServiceVersionLinks)
                .filter(QueryBuilders.rangeQuery("created").lt(monthAgo));

        // 총 요구사항 개수를 구하기 위한 쿼리
        BoolQueryBuilder boolQueryForTotalRequirements = es.boolQueryBuilder(pdServiceLink, pdServiceVersionLinks)
                .filter(QueryBuilders.rangeQuery("created").lt(monthAgo))
                .filter(QueryBuilders.termQuery("isReq", true));

        // 이슈 상태에 대한 집계 쿼리
        TermsAggregationBuilder totalAggregationBuilder = AggregationBuilders.terms("total_status").field("status.status_name.keyword");

        // 총 이슈 개수 검색
        SearchSourceBuilder sourceBuilderForTotalIssues = new SearchSourceBuilder().query(boolQueryForTotalIssues).aggregation(totalAggregationBuilder);
        SearchResponse searchResponseForTotalIssues = 지라이슈저장소.search(es.getSearchRequest(sourceBuilderForTotalIssues), RequestOptions.DEFAULT);
        long totalIssuesCount = searchResponseForTotalIssues.getHits().getTotalHits().value;

        // 총 요구사항 개수 검색
        SearchSourceBuilder sourceBuilderForTotalRequirements = new SearchSourceBuilder().query(boolQueryForTotalRequirements);
        SearchResponse searchResponseForTotalRequirements = 지라이슈저장소.search(es.getSearchRequest(sourceBuilderForTotalRequirements), RequestOptions.DEFAULT);
        long totalRequirementsCount = searchResponseForTotalRequirements.getHits().getTotalHits().value;

        // 이슈 상태 집계 결과 가져오기
        Terms totalStatus = searchResponseForTotalIssues.getAggregations().get("total_status");
        Map<String, Long> statusMap = new HashMap<>();
        for (Terms.Bucket entry : totalStatus.getBuckets()) {
            String key = entry.getKeyAsString();
            long value = entry.getDocCount();
            statusMap.put(key, value);
        }

        return new 요구사항_지라이슈상태_주별_집계(totalIssuesCount, statusMap, totalRequirementsCount);
    }

    @Override
    public Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항_지라이슈상태_주별_집계(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException {
        LocalDate now = LocalDate.now(ZoneId.of("UTC"));
        LocalDate monthAgo = now.minusWeeks(4);

        // 1. 검색 범위에 포함되지 않은 누적 데이터를 가져온다. 기간이 길어지면 길어질수록 무의미한, 반복적인 연산이기 때문에 캐싱 고려
        요구사항_지라이슈상태_주별_집계 HistoricalData = 누적데이터조회(pdServiceLink, pdServiceVersionLinks, monthAgo);
        Map<String, Long> totalStatuses = new HashMap<>(HistoricalData.getStatuses());
        long totalIssues = HistoricalData.getTotalIssues();
        long totalRequirements = HistoricalData.getTotalRequirements();

        // 2. 검색 범위 내의 데이터를 가져온다. 현재 검색 범위는 차트 UI를 고려하여, 4~5주 정도로 적용
        BoolQueryBuilder boolQuery = es.boolQueryBuilder(pdServiceLink, pdServiceVersionLinks)
                .filter(QueryBuilders.rangeQuery("created").from(monthAgo).to(now));

        DateHistogramAggregationBuilder weeklyAggregationBuilder = AggregationBuilders
                .dateHistogram("aggregation_by_week")
                .field("created")
                .calendarInterval(DateHistogramInterval.WEEK)
                .subAggregation(AggregationBuilders.terms("statuses").field("status.status_name.keyword"))
                .subAggregation(AggregationBuilders.terms("requirements").field("isReq"));

        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource().query(boolQuery).aggregation(weeklyAggregationBuilder);
        SearchResponse searchResponse = 지라이슈저장소.search(es.getSearchRequest(sourceBuilder), RequestOptions.DEFAULT);

        Histogram aggregationByMonth = searchResponse.getAggregations().get("aggregation_by_week");


        Map<String, 요구사항_지라이슈상태_주별_집계> 검색결과 = aggregationByMonth.getBuckets().stream()
                .sorted(Comparator.comparing(bucket -> OffsetDateTime.parse(bucket.getKeyAsString()).toLocalDate()))
                .collect(Collectors.toMap(
                        entry -> transformDate(entry.getKeyAsString()),
                        this::주별데이터생성,
                        (existingValue, newValue) -> existingValue,
                        LinkedHashMap::new
                ));

        // 3. 검색 데이터에 누적 데이터를 더해준다.
        for (요구사항_지라이슈상태_주별_집계 monthlyAggregation : 검색결과.values()) {
            totalIssues += monthlyAggregation.getTotalIssues();
            totalRequirements += monthlyAggregation.getTotalRequirements();
            monthlyAggregation.setTotalIssues(totalIssues);
            monthlyAggregation.setTotalRequirements(totalRequirements);

            Map<String, Long> currentStatuses = monthlyAggregation.getStatuses();
            for (Map.Entry<String, Long> entry : currentStatuses.entrySet()) {
                totalStatuses.merge(entry.getKey(), entry.getValue(), Long::sum);
            }

            monthlyAggregation.setStatuses(new HashMap<>(totalStatuses));
        }

        return 검색결과;
    }

    private 요구사항_지라이슈상태_주별_집계 주별데이터생성(Histogram.Bucket entry) {

        Map<String, Long> statuses = ((Terms) entry.getAggregations().get("statuses")).getBuckets().stream()
                .collect(Collectors.toMap(Terms.Bucket::getKeyAsString, Terms.Bucket::getDocCount));

        long totalReqCount = Optional.ofNullable((Terms) entry.getAggregations().get("requirements"))
                .flatMap(reqs -> reqs.getBuckets().stream()
                        .filter(bucket -> "true".equals(bucket.getKeyAsString()))
                        .findFirst())
                .map(Terms.Bucket::getDocCount)
                .orElse(0L);

        return new 요구사항_지라이슈상태_주별_집계(entry.getDocCount(), statuses, totalReqCount);
    }

    private String transformDate(String date) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(date);
        return DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .format(offsetDateTime);
    }
}
