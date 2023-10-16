package com.arms.elasticsearch.services;

import com.arms.elasticsearch.helper.인덱스자료;
import com.arms.elasticsearch.models.요구사항_지라이슈상태_월별_집계;
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
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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


    @Override
    public Map<String, 요구사항_지라이슈상태_월별_집계> 요구사항_지라이슈상태_월별_집계(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException {
        Instant now = Instant.now();
        Instant oneYearAgo = now.minus(365, ChronoUnit.DAYS);

        // 현재 시간("created field", UTC) 기준 최근 12개월 데이터만 집계
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.termQuery("pdServiceId", pdServiceLink))
                .filter(QueryBuilders.rangeQuery("created")
                        .from(oneYearAgo.toString()).to(now.toString())
                );

        if (pdServiceVersionLinks != null && !pdServiceVersionLinks.isEmpty()) {
            boolQuery.filter(QueryBuilders.termsQuery("pdServiceVersion", pdServiceVersionLinks));
        }

        // DateHistogramAggregation 설정
        DateHistogramAggregationBuilder dateHistogramAgg = AggregationBuilders
                .dateHistogram("aggregation_by_month")
                .field("created")
                .calendarInterval(DateHistogramInterval.MONTH);

        // 요구사항과 지라 이슈 상태에 대한 집계
        dateHistogramAgg.subAggregation(AggregationBuilders.terms("statuses").field("status.status_name.keyword"));
        dateHistogramAgg.subAggregation(AggregationBuilders.terms("requirements").field("isReq"));

        // Query 및 Aggregation 반영
        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource().query(boolQuery).aggregation(dateHistogramAgg);

        // Elasticsearch 쿼리 실행
        SearchResponse searchResponse = 지라이슈저장소.search(getSearchRequest(sourceBuilder), RequestOptions.DEFAULT);

        // Aggregation 결과 처리
        Histogram issuesByMonth = searchResponse.getAggregations().get("aggregation_by_month");

        // 월 별 요구사항, 지라 이슈 상태 집계
        Map<String, 요구사항_지라이슈상태_월별_집계> monthlyDatas = issuesByMonth.getBuckets().stream()
                .collect(Collectors.toMap(
                        Histogram.Bucket::getKeyAsString,
                        this::createMonthlyData
                ));


        // 월별 누적 처리
        long totalIssues = 0;
        long totalRequirements = 0;
        for (요구사항_지라이슈상태_월별_집계 monthlyData : monthlyDatas.values()) {
            totalIssues += monthlyData.getTotalIssues();
            totalRequirements += monthlyData.getTotalRequirements();
            monthlyData.setTotalIssues(totalIssues);
            monthlyData.setTotalRequirements(totalRequirements);
        }

        // 날짜 형식 변환 2023-10-01T00:00:00.000Z -> 2023-10
        return monthlyDatas.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> transformDateKey(entry.getKey()),
                        Map.Entry::getValue
                ));
    }

    public 요구사항_지라이슈상태_월별_집계 createMonthlyData(Histogram.Bucket entry) {
        요구사항_지라이슈상태_월별_집계 monthlyData = new 요구사항_지라이슈상태_월별_집계();
        monthlyData.setTotalIssues(entry.getDocCount());

        Map<String, Long> statuses = ((Terms) entry.getAggregations().get("statuses")).getBuckets().stream()
                .collect(Collectors.toMap(Terms.Bucket::getKeyAsString, Terms.Bucket::getDocCount));

        monthlyData.setStatuses(statuses);

        long totalReqCount = Optional.ofNullable((Terms) entry.getAggregations().get("requirements"))
                .flatMap(reqs -> reqs.getBuckets().stream()
                        .filter(bucket -> "true".equals(bucket.getKeyAsString()))
                        .findFirst())
                .map(Terms.Bucket::getDocCount)
                .orElse(0L);

        monthlyData.setTotalRequirements(totalReqCount);
        return monthlyData;
    }

    public String transformDateKey(String month) {
        Instant instant = Instant.parse(month);
        return DateTimeFormatter
                .ofPattern("yyyy-MM")
                .withZone(ZoneId.of("UTC"))
                .format(instant);
    }

    public static SearchRequest getSearchRequest(SearchSourceBuilder sourceBuilder) {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(인덱스자료.지라이슈_인덱스명);
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }
}
