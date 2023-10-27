package com.arms.elasticsearch.services.dashboard.bar;//package com.arms.elasticsearch.services.dashboard.bar;
//
//import com.arms.elasticsearch.helper.인덱스자료;
//import com.arms.elasticsearch.models.dashboard.bar.요구사항_지라이슈상태_주별_집계;
//import com.arms.elasticsearch.repositories.지라이슈_저장소;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
//import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
//import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.time.Instant;
//import java.time.LocalDate;
//import java.time.OffsetDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service("지라이슈_대시보드_월_별_제품의_요구사항_및_이슈상태_누적_현황")
//@AllArgsConstructor
//public class BarChartImplSe implements BarChart {
//    public static final String STATUS_AGG_NAME = "statuses";
//    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());
//
//    private 지라이슈_저장소 지라이슈저장소;
//
//    public static SearchRequest getSearchRequest(SearchSourceBuilder sourceBuilder) {
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices(인덱스자료.지라이슈_인덱스명);
//        searchRequest.source(sourceBuilder);
//        return searchRequest;
//    }
//
//    public 요구사항_지라이슈상태_주별_집계 fetchTotalPastData(Long pdServiceLink, List<Long> pdServiceVersionLinks, LocalDate monthAgo) throws IOException {
//
//        // 총 이슈 개수를 구하기 위한 쿼리
//        BoolQueryBuilder boolQueryForTotalIssues = QueryBuilders.boolQuery()
//                .filter(QueryBuilders.termQuery("pdServiceId", pdServiceLink))
//                .filter(QueryBuilders.rangeQuery("created").lt(monthAgo));
//
//        // 총 요구사항 개수를 구하기 위한 쿼리
//        BoolQueryBuilder boolQueryForTotalRequirements = QueryBuilders.boolQuery()
//                .filter(QueryBuilders.termQuery("pdServiceId", pdServiceLink))
//                .filter(QueryBuilders.rangeQuery("created").lt(monthAgo))
//                .filter(QueryBuilders.termQuery("isReq", true));
//
//        if(pdServiceVersionLinks != null && !pdServiceVersionLinks.isEmpty()) {
//            boolQueryForTotalIssues.filter(QueryBuilders.termsQuery("pdServiceVersion", pdServiceVersionLinks));
//            boolQueryForTotalRequirements.filter(QueryBuilders.termsQuery("pdServiceVersion", pdServiceVersionLinks));
//        }
//
//
//        // 이슈 상태에 대한 집계 쿼리
//        TermsAggregationBuilder totalAggregationBuilder = AggregationBuilders
//                .terms("total_status")
//                .field("status.status_name.keyword");
//
//        // 총 이슈 개수 검색
//        SearchSourceBuilder sourceBuilderForTotalIssues = new SearchSourceBuilder().query(boolQueryForTotalIssues).aggregation(totalAggregationBuilder);
//        SearchResponse searchResponseForTotalIssues = 지라이슈저장소.search(getSearchRequest(sourceBuilderForTotalIssues), RequestOptions.DEFAULT);
//        long totalIssuesCount = searchResponseForTotalIssues.getHits().getTotalHits().value;
//
//        // 총 요구사항 개수 검색
//        SearchSourceBuilder sourceBuilderForTotalRequirements = new SearchSourceBuilder().query(boolQueryForTotalRequirements);
//        SearchResponse searchResponseForTotalRequirements = 지라이슈저장소.search(getSearchRequest(sourceBuilderForTotalRequirements), RequestOptions.DEFAULT);
//        long totalRequirementsCount = searchResponseForTotalRequirements.getHits().getTotalHits().value;
//
//        // 이슈 상태 집계 결과 가져오기
//        Terms totalStatus = searchResponseForTotalIssues.getAggregations().get("total_status");
//        Map<String, Long> statusMap = new HashMap<>();
//        for (Terms.Bucket entry : totalStatus.getBuckets()) {
//            String key = entry.getKeyAsString();  // 키
//            long value = entry.getDocCount(); // 문서 개수
//            statusMap.put(key, value);
//        }
//
//        // 결과 객체 생성 및 값 설정
//        요구사항_지라이슈상태_주별_집계 totalAggregation = new 요구사항_지라이슈상태_주별_집계();
//        totalAggregation.setStatuses(statusMap);
//        totalAggregation.setTotalIssues(totalIssuesCount);
//        totalAggregation.setTotalRequirements(totalRequirementsCount);
//
//        return totalAggregation;
//    }
//
//    @Override
//    public Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항_지라이슈상태_월별_집계(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException {
//        LocalDate now = LocalDate.now(ZoneId.of("UTC"));
//        LocalDate monthAgo = now.minusWeeks(4);
//
//        // 1. 검색 범위에 포함되지 않은 누적 데이터를 가져온다. 기간이 길어지면 길어질수록 무의미한, 반복적인 연산이기 때문에, 월, 주, 일 별 Redis Cache를 활용하는 방안은?
//        요구사항_지라이슈상태_주별_집계 HistoricalData = fetchTotalPastData(pdServiceLink, pdServiceVersionLinks, monthAgo);
//        Map<String, Long> totalStatuses = new HashMap<>(HistoricalData.getStatuses());
//        long totalIssues = HistoricalData.getTotalIssues();
//        long totalRequirements = HistoricalData.getTotalRequirements();
//
//        // 2. 검색 범위 내의 데이터를 가져온다. 현재 검색 범위는 차트 UI를 고려하여, 4~5주 정도로 생각중이다.
//        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
//                .filter(QueryBuilders.termQuery("pdServiceId", pdServiceLink))
//                .filter(QueryBuilders.rangeQuery("created")
//                        .from(monthAgo).to(now)
//                );
//
//        if(pdServiceVersionLinks != null && !pdServiceVersionLinks.isEmpty()) {
//            boolQuery.filter(QueryBuilders.termsQuery("pdServiceVersion", pdServiceVersionLinks));
//        }
//
//        DateHistogramAggregationBuilder monthlyAggregationBuilder = AggregationBuilders
//                .dateHistogram("aggregation_by_week")
//                .field("created")
//                .calendarInterval(DateHistogramInterval.WEEK)
//                .subAggregation(AggregationBuilders.terms(STATUS_AGG_NAME).field("status.status_name.keyword"))
//                .subAggregation(AggregationBuilders.terms("requirements").field("isReq"));
//
//        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource().query(boolQuery).aggregation(monthlyAggregationBuilder);
//        SearchResponse searchResponse = 지라이슈저장소.search(getSearchRequest(sourceBuilder), RequestOptions.DEFAULT);
//        Histogram aggregationByMonth = searchResponse.getAggregations().get("aggregation_by_week");
//
//        Map<String, 요구사항_지라이슈상태_주별_집계> result = aggregationByMonth.getBuckets().stream()
//                .sorted(Comparator.comparing(bucket -> Instant.parse(bucket.getKeyAsString()).atZone(ZoneId.of("UTC")).toLocalDate()))
//                .collect(Collectors.toMap(
//                        entry -> transformDateKey(entry.getKeyAsString()),
//                        this::createWeeklyData,
//                        (e1, e2) -> e1,
//                        LinkedHashMap::new
//                ));
//
//        for (요구사항_지라이슈상태_주별_집계 monthlyAggregation : result.values()) {
//            totalIssues += monthlyAggregation.getTotalIssues();
//            totalRequirements += monthlyAggregation.getTotalRequirements();
//            monthlyAggregation.setTotalIssues(totalIssues);
//            monthlyAggregation.setTotalRequirements(totalRequirements);
//
//            Map<String, Long> currentStatuses = monthlyAggregation.getStatuses();
//            for (Map.Entry<String, Long> entry : currentStatuses.entrySet()) {
//                totalStatuses.merge(entry.getKey(), entry.getValue(), Long::sum);
//            }
//
//            monthlyAggregation.setStatuses(new HashMap<>(totalStatuses));
//        }
//
//        return result;
//    }
//
//    public 요구사항_지라이슈상태_주별_집계 createWeeklyData(Histogram.Bucket entry) {
//        요구사항_지라이슈상태_주별_집계 weeklyData = new 요구사항_지라이슈상태_주별_집계();
//        weeklyData.setTotalIssues(entry.getDocCount());
//
//        Map<String, Long> statuses = ((Terms) entry.getAggregations().get(STATUS_AGG_NAME)).getBuckets().stream()
//                .collect(Collectors.toMap(Terms.Bucket::getKeyAsString, Terms.Bucket::getDocCount));
//
//        weeklyData.setStatuses(statuses);
//
//        long totalReqCount = Optional.ofNullable((Terms) entry.getAggregations().get("requirements"))
//                .flatMap(reqs -> reqs.getBuckets().stream()
//                        .filter(bucket -> "true".equals(bucket.getKeyAsString()))
//                        .findFirst())
//                .map(Terms.Bucket::getDocCount)
//                .orElse(0L);
//
//        weeklyData.setTotalRequirements(totalReqCount);
//        return weeklyData;
//    }
//
//    public String transformDateKey(String day) {
//        OffsetDateTime offsetDateTime = OffsetDateTime.parse(day);
//        return DateTimeFormatter
//                .ofPattern("yyyy-MM-dd")
//                .withZone(ZoneId.of("UTC"))
//                .format(offsetDateTime);
//    }
//}
