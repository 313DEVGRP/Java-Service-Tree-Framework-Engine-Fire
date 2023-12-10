package com.arms.api.engine.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.arms.api.engine.dtos.트리맵_담당자_요구사항_기여도;
import com.arms.api.engine.dtos.요구사항_지라이슈상태_일별_집계;
import com.arms.api.engine.dtos.요구사항_지라이슈상태_주별_집계;
import com.arms.api.engine.models.지라이슈;
import com.arms.api.engine.models.지라이슈_제품_및_제품버전_검색요청;
import com.arms.elasticsearch.helper.인덱스자료;
import com.arms.elasticsearch.util.aggregation.CustomAbstractAggregationBuilder;
import com.arms.elasticsearch.util.aggregation.CustomDateHistogramAggregationBuilder;
import com.arms.elasticsearch.util.aggregation.CustomTermsAggregationBuilder;
import com.arms.elasticsearch.util.query.*;
import com.arms.elasticsearch.util.query.bool.ExistsQueryFilter;
import com.arms.elasticsearch.util.query.bool.RangeQueryFilter;
import com.arms.elasticsearch.util.query.bool.TermQueryMust;
import com.arms.elasticsearch.util.query.bool.TermsQueryFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.arms.api.engine.repositories.지라이슈_저장소;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색결과_목록_메인;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("지라이슈_대시보드_서비스")
@AllArgsConstructor
public class 지라이슈_검색엔진_대시보드 implements 지라이슈_대시보드_서비스 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라이슈_저장소 지라이슈저장소;

    private com.arms.elasticsearch.helper.인덱스_유틸 인덱스_유틸;

    private ObjectMapper objectMapper;

    private ElasticsearchOperations 엘라스틱서치_작업;

    @Autowired
    private RestHighLevelClient client;

    @Override
    public Map<String, Long> 제품서비스별_담당자_이름_통계(Long 지라서버_아이디, Long 제품서비스_아이디) {

        BoolQueryBuilder 복합조회 = QueryBuilders.boolQuery();
        if (제품서비스_아이디 != null && 제품서비스_아이디 > 9L) {
            MatchQueryBuilder 제품서비스_조회 = QueryBuilders.matchQuery("pdServiceId", 제품서비스_아이디);
            복합조회.must(제품서비스_조회);
        }

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
            .withQuery(복합조회)
            .withAggregations(
                AggregationBuilders
                    .terms("담당자별_집계").field("assignee.assignee_displayName.keyword")
            ).withMaxResults(0);

        // 요구사항 vs 연결된이슈&서브테스크 구분안하고 한번에
        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());
        Long 결과 = 검색결과_목록_메인.get전체합계();
        로그.info("검색결과 개수: " + 결과);

        List<검색결과> 담당자별_집계 = 검색결과_목록_메인.get검색결과().get("담당자별_집계");

        long 담당자_총합 = 0;
        Map<String, Long> 제품서비스별_하위이슈_담당자_집계 = new HashMap<>();
        for (검색결과 담당자 : 담당자별_집계) {
            String 담당자_이메일 = 담당자.get필드명();
            long 개수 = 담당자.get개수();
            log.info("담당자: " + 담당자_이메일 + ", Count: " + 개수);
            담당자_총합 += 개수;
            제품서비스별_하위이슈_담당자_집계.put(담당자_이메일, 개수);
        }
        제품서비스별_하위이슈_담당자_집계.put("담당자 미지정", 결과 - 담당자_총합);

        return 제품서비스별_하위이슈_담당자_집계;
    }


    @Override
    public Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별집계(Long pdServiceLink) {


        MatchQueryBuilder 제품아이디별_조회 = QueryBuilders.matchQuery("pdServiceId", pdServiceLink);

        TermsAggregationBuilder 담당자별_집계
                = AggregationBuilders.terms("담당자별_집계")
                    .field("assignee.assignee_emailAddress.keyword")
                    .subAggregation(AggregationBuilders.terms("요구사항_여부별_집계").field("isReq")
                    .subAggregation(AggregationBuilders.terms("상태별_집계").field("status.status_name.keyword")));

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(제품아이디별_조회)
                .withAggregations(담당자별_집계)
                .withMaxResults(0);

        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());

        Map<String, Map<String, Map<String, Integer>>> 담당자별_요구사항여부별_상태값_집계
                = 검색결과_목록_메인.get검색결과().get("담당자별_집계")
                    .stream()
                    .collect(
                        Collectors.toMap(
                        검색결과::get필드명,
                        담당자 -> {
                            List<검색결과> 요구사항_여부별_집계 = 담당자.get하위검색결과().get("요구사항_여부별_집계");
                            return 요구사항_여부별_집계.stream()
                                    .collect(Collectors.toMap(
                                    검색결과 -> {
                                        String 여부 = 검색결과.get필드명();
                                        if (여부.equals("true")) {
                                            return "requirement";
                                        } else {
                                            return "relation_issue";
                                        }
                                    },
                                    검색결과 -> {
                                        List<검색결과> 상태별_집계 = 검색결과.get하위검색결과().get("상태별_집계");
                                        return 상태별_집계.stream()
                                            .collect(Collectors.toMap(
                                                    a->a.get필드명(),
                                                    a->(int)a.get개수()));
                                    }
                            ));
                        }
        ));

        return 담당자별_요구사항여부별_상태값_집계;
    }


    @Override
    public 검색결과_목록_메인 집계결과_가져오기(쿼리_추상_팩토리 쿼리추상팩토리) {

        return 지라이슈저장소.aggregationSearch(
            쿼리추상팩토리.생성()
        );
    }


    @Override
    public List<검색결과> 제품_버전별_담당자_목록(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청)  {
        EsQuery esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", 지라이슈_제품_및_제품버전_검색요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersion", 지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks()),
                        new ExistsQueryFilter("assignee")
                );

        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

        CustomAbstractAggregationBuilder versionsAgg = new CustomTermsAggregationBuilder("versions")
                .field("pdServiceVersion")
                .addSubAggregation(
                        new CustomTermsAggregationBuilder("assignees")
                                .field("assignee.assignee_accountId.keyword")
                                .order(BucketOrder.count(false))
                                .size(지라이슈_제품_및_제품버전_검색요청.get크기())
                                .addSubAggregation(AggregationBuilders.terms("displayNames").field("assignee.assignee_displayName.keyword"))
                                .build()
                );

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withAggregations(versionsAgg.build())
                .build();

        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(searchQuery);

        return 검색결과_목록_메인.get검색결과().get("versions");

    }


    @Override
    public List<트리맵_담당자_요구사항_기여도> 작업자_별_요구사항_별_관여도(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청) {
        List<지라이슈> requirementIssues = 지라이슈저장소.findByIsReqAndPdServiceIdAndPdServiceVersionIn(true, 지라이슈_제품_및_제품버전_검색요청.getPdServiceLink(), 지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks());

        List<String> allReqKeys = requirementIssues.stream().map(지라이슈::getKey).collect(Collectors.toList());

        List<지라이슈> allSubTasks = 지라이슈저장소.findByParentReqKeyIn(allReqKeys);

        Map<String, List<지라이슈>> subTasksByParent = allSubTasks.stream()
                .filter(subtask -> subtask.getAssignee() != null)
                .collect(Collectors.groupingBy(지라이슈::getParentReqKey));

        Map<String, 트리맵_담당자_요구사항_기여도> response = new HashMap<>();

        requirementIssues.forEach(issue -> {
            String issueKey = issue.getKey();
            String issueSummary = issue.getSummary();

            subTasksByParent.getOrDefault(issueKey, Collections.emptyList()).stream()
                    .collect(Collectors.groupingBy(subTask -> subTask.getAssignee().getAccountId() + "_" + subTask.getAssignee().getDisplayName(), Collectors.counting()))
                    .forEach((key, value) -> {
                        String[] assigneeInfo = key.split("_");
                        String assigneeId = assigneeInfo[0];
                        String assigneeDisplayName = assigneeInfo[1];

                        트리맵_담당자_요구사항_기여도 트리맵담당자요구사항기여도 = response.computeIfAbsent(assigneeId, k -> createAssigneeContribution(assigneeDisplayName));

                        트리맵담당자요구사항기여도.setValue(트리맵담당자요구사항기여도.getValue() + value);

                        Map<String, Object> issueMap = createIssueMap(issueSummary, assigneeDisplayName, value);

                        트리맵담당자요구사항기여도.getChildren().add(issueMap);
                    });
        });

        return response.values().stream()
                .sorted(Comparator.comparingLong(트리맵_담당자_요구사항_기여도::getValue).reversed())
                .collect(Collectors.toList());
    }

    private 트리맵_담당자_요구사항_기여도 createAssigneeContribution(String assigneeDisplayName) {
        트리맵_담당자_요구사항_기여도 트리맵담당자요구사항기여도 = new 트리맵_담당자_요구사항_기여도();
        트리맵담당자요구사항기여도.setName(assigneeDisplayName);
        트리맵담당자요구사항기여도.setPath(assigneeDisplayName);
        트리맵담당자요구사항기여도.setValue(0L);
        트리맵담당자요구사항기여도.setChildren(new ArrayList<>());
        return 트리맵담당자요구사항기여도;
    }

    private Map<String, Object> createIssueMap(String issueSummary, String assigneeDisplayName, Long value) {
        Map<String, Object> issueMap = new HashMap<>();
        issueMap.put("name", issueSummary);
        issueMap.put("path", assigneeDisplayName + "/" + issueSummary);
        issueMap.put("value", value);
        return issueMap;
    }


    @Override
    public Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항_지라이슈상태_주별_집계(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청) {
        LocalDate now = LocalDate.now(ZoneId.of("UTC"));
        LocalDate monthAgo = now.minusWeeks(4);

        // 1. 검색 범위에 포함되지 않은 누적 데이터를 가져온다. 기간이 길어지면 길어질수록 무의미한, 반복적인 연산이기 때문에 캐싱 고려
        요구사항_지라이슈상태_주별_집계 HistoricalData = 누적데이터조회(
                지라이슈_제품_및_제품버전_검색요청.getPdServiceLink(),
                지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks(),
                monthAgo
        );
        Map<String, Long> totalStatuses = new HashMap<>(HistoricalData.getStatuses());
        long totalIssues = HistoricalData.getTotalIssues();
        long totalRequirements = HistoricalData.getTotalRequirements();

        // 2. 검색 범위 내의 데이터를 가져온다. 현재 검색 범위는 차트 UI를 고려하여, 4~5주 정도로 적용
        EsQuery esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", 지라이슈_제품_및_제품버전_검색요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersion", 지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks()),
                        new RangeQueryFilter("created", monthAgo, now, "fromto")
                );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

        CustomAbstractAggregationBuilder weeklyAggregationBuilder = new CustomDateHistogramAggregationBuilder("aggregation_by_week")
                .field("created")
                .calendarInterval(DateHistogramInterval.WEEK)
                .addSubAggregation(new CustomTermsAggregationBuilder("statuses").field("status.status_name.keyword").build())
                .addSubAggregation(new CustomTermsAggregationBuilder("requirements").field("isReq").build());

        NativeSearchQueryBuilder nativeSearchQueryBuilder
                = new NativeSearchQueryBuilder().withQuery(boolQuery).withAggregations(weeklyAggregationBuilder.build());

        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());

        List<검색결과> aggregationByWeek = 검색결과_목록_메인.get검색결과().get("aggregation_by_week");


        Map<String, 요구사항_지라이슈상태_주별_집계> 검색결과 = aggregationByWeek.stream()
                .sorted(Comparator.comparing(bucket -> OffsetDateTime.parse(bucket.get필드명()).toLocalDate()))
                .collect(Collectors.toMap(
                        entry -> transformDate(entry.get필드명()),
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

    public 요구사항_지라이슈상태_주별_집계 누적데이터조회(Long pdServiceLink, List<Long> pdServiceVersionLinks, LocalDate monthAgo) {
        // 총 이슈 개수를 구하기 위한 쿼리
        EsQuery issueEsQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", pdServiceLink),
                        new TermsQueryFilter("pdServiceVersion", pdServiceVersionLinks),
                        new RangeQueryFilter("created", null, monthAgo, "lt")
                );
        BoolQueryBuilder boolQueryForTotalIssues = issueEsQuery.getQuery(new ParameterizedTypeReference<>() {});

        // 총 요구사항 개수를 구하기 위한 쿼리
        EsQuery reqEsQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", pdServiceLink),
                        new TermQueryMust("isReq", true),
                        new RangeQueryFilter("created", null, monthAgo, "lt")
                );
        BoolQueryBuilder boolQueryForTotalRequirements = reqEsQuery.getQuery(new ParameterizedTypeReference<>() {});

        // 이슈 상태에 대한 집계 쿼리
        TermsAggregationBuilder totalAggregationBuilder = AggregationBuilders.terms("total_status").field("status.status_name.keyword");

        // 총 이슈 개수 검색
        NativeSearchQueryBuilder nativeSearchQueryBuilderForTotalIssues
                = new NativeSearchQueryBuilder().withQuery(boolQueryForTotalIssues).withAggregations(totalAggregationBuilder);

        검색결과_목록_메인 searchResponseForTotalIssues
                = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilderForTotalIssues.build());

        Long totalIssuesCount
                = searchResponseForTotalIssues.get전체합계();

        // 총 요구사항 개수 검색
        NativeSearchQueryBuilder nativeSearchQueryBuilderForTotalRequirements
                = new NativeSearchQueryBuilder().withQuery(boolQueryForTotalRequirements);

        Long totalRequirementsCount = Long.valueOf(Optional.ofNullable(지라이슈저장소.normalSearch(nativeSearchQueryBuilderForTotalRequirements.build()))
                .map(지라이슈 -> 지라이슈.size())
                .orElse(0));

        // 이슈 상태 집계 결과 가져오기
        Map<String, Long> statusMap = new HashMap<>();
        Optional.ofNullable(searchResponseForTotalIssues)
                .map(response -> response.get검색결과().get("total_status"))
                .ifPresent(totalStatus -> {
                    for (검색결과 검색결과 : totalStatus) {
                        String key = 검색결과.get필드명();
                        long value = 검색결과.get개수();
                        statusMap.put(key, value);
                    }
                });

        return new 요구사항_지라이슈상태_주별_집계(totalIssuesCount, statusMap, totalRequirementsCount);
    }

    private 요구사항_지라이슈상태_주별_집계 주별데이터생성(검색결과 검색_결과) {

        Map<String, Long> statuses = (검색_결과.get하위검색결과().get("statuses")).stream()
                .collect(Collectors.toMap(검색결과::get필드명, 검색결과::get개수));

        long totalReqCount = Optional.ofNullable(검색_결과.get하위검색결과().get("requirements"))
                .flatMap(reqs -> reqs.stream()
                        .filter(bucket -> "true".equals(bucket.get필드명()))
                        .findFirst())
                .map(검색결과::get개수)
                .orElse(0L);

        return new 요구사항_지라이슈상태_주별_집계(검색_결과.get개수(), statuses, totalReqCount);
    }

    private String transformDate(String date) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(date);
        return DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .format(offsetDateTime);
    }

    @Override
    public Map<String, 요구사항_지라이슈상태_주별_집계> 일자별_지라이슈_생성개수_및_상태_집계(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청, String startDate) {

        Map<String, 요구사항_지라이슈상태_주별_집계> 생성개수_집계결과 = 일자별_이슈_생성개수_집계(지라이슈_제품_및_제품버전_검색요청, startDate);

        Map<String, 요구사항_지라이슈상태_주별_집계> 상태_집계결과 = 요구사항_지라이슈상태_일별_집계(지라이슈_제품_및_제품버전_검색요청, startDate);

        상태_집계결과.forEach((date, 일별_상태) -> {
            // 생성개수_집계결과에서 해당 날짜를 찾고, 없다면 새 인스턴스를 생성
            if (일별_상태.getStatuses() != null) {
                요구사항_지라이슈상태_주별_집계 생성개수_집계 = 생성개수_집계결과.computeIfAbsent(date, k -> new 요구사항_지라이슈상태_주별_집계());

                // 주별_집계의 statuses에 일별_상태를 추가
                if (생성개수_집계.getStatuses() == null) {
                    생성개수_집계.setStatuses(new HashMap<>());
                }

                생성개수_집계.getStatuses().putAll(일별_상태.getStatuses());
            }
        });

        return 생성개수_집계결과;
    }

    @Override
    public Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항_지라이슈상태_일별_집계(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청, String startDate) {

        EsQuery esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", 지라이슈_제품_및_제품버전_검색요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersion", 지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks())
//                        , new RangeQueryFilter("updated", 버전_시작일, now, "fromto")
                );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

        CustomAbstractAggregationBuilder dailyAggregationBuilder = new CustomDateHistogramAggregationBuilder("aggregation_by_day")
                .field("updated")
                .calendarInterval(DateHistogramInterval.DAY)
                .addSubAggregation(new CustomTermsAggregationBuilder("statuses").field("status.status_name.keyword").build());

        NativeSearchQueryBuilder nativeSearchQueryBuilder
                = new NativeSearchQueryBuilder().withQuery(boolQuery).withAggregations(dailyAggregationBuilder.build());

        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());

        List<검색결과> aggregationByDay = 검색결과_목록_메인.get검색결과().get("aggregation_by_day");


        Map<String, 요구사항_지라이슈상태_주별_집계> 검색결과 = aggregationByDay.stream()
                .sorted(Comparator.comparing(bucket -> OffsetDateTime.parse(bucket.get필드명()).toLocalDate()))
                .collect(Collectors.toMap(
                        entry -> transformDate(entry.get필드명()),
                        this::일별데이터생성,
                        (existingValue, newValue) -> existingValue,
                        LinkedHashMap::new
                ));

        return 검색결과;
    }

    private 요구사항_지라이슈상태_주별_집계 일별데이터생성(검색결과 검색_결과) {

        Map<String, Long> statuses = (검색_결과.get하위검색결과().get("statuses")).stream()
                .collect(Collectors.toMap(검색결과::get필드명, 검색결과::get개수));

        return new 요구사항_지라이슈상태_주별_집계(0, statuses, 0);
    }

    public Map<String, 요구사항_지라이슈상태_주별_집계> 일자별_이슈_생성개수_집계(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청, String startDate) {

        EsQuery esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", 지라이슈_제품_및_제품버전_검색요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersion", 지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks())
                );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

        CustomAbstractAggregationBuilder dailyAggregationBuilder = new CustomDateHistogramAggregationBuilder("aggregation_by_day")
                .field("created")
                .calendarInterval(DateHistogramInterval.DAY)
                .addSubAggregation(new CustomTermsAggregationBuilder("요구사항여부").field("isReq").build());

        NativeSearchQueryBuilder nativeSearchQueryBuilder
                = new NativeSearchQueryBuilder().withQuery(boolQuery).withAggregations(dailyAggregationBuilder.build());

        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());

        List<검색결과> aggregationByDay = 검색결과_목록_메인.get검색결과().get("aggregation_by_day");


        Map<String, 요구사항_지라이슈상태_주별_집계> 검색결과 = aggregationByDay.stream()
                .sorted(Comparator.comparing(bucket -> OffsetDateTime.parse(bucket.get필드명()).toLocalDate()))
                .collect(Collectors.toMap(
                        entry -> transformDate(entry.get필드명()),
                        this::일별_개수_및_상태_데이터생성,
                        (existingValue, newValue) -> existingValue,
                        LinkedHashMap::new
                ));

        return 검색결과;
    }

    private 요구사항_지라이슈상태_주별_집계 일별_개수_및_상태_데이터생성(검색결과 결과) {
        long totalRequirement = 0;
        long totalIssue = 0;

        Map<String, Long> isReqTerms = 결과.get하위검색결과().get("요구사항여부").stream()
                .collect(Collectors.toMap(검색결과::get필드명, 검색결과::get개수, Long::sum, LinkedHashMap::new));

        if (isReqTerms.size() > 0) {
            totalRequirement = isReqTerms.getOrDefault("true", 0L);
            totalIssue = isReqTerms.getOrDefault("false", 0L);
        }

        return new 요구사항_지라이슈상태_주별_집계(totalIssue, null, totalRequirement);
    }

    public List<지라이슈> 제품서비스_버전목록으로_주간_업데이트된_이슈조회(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청, Integer baseWeek){

        if (baseWeek < 1) {
            baseWeek = 1;
        }

        String from = "now-"+baseWeek+"w/d";

        String to   = "now-"+(baseWeek-1)+"w/d";

        EsQuery esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", 지라이슈_제품_및_제품버전_검색요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersion", 지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks()),
                        new RangeQueryFilter("updated", from, to, "fromto")
                );

        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQuery);
        sourceBuilder.size(10000);

        List<지라이슈> 전체결과 = new ArrayList<>();
        String 지라인덱스 = 인덱스자료.지라이슈_인덱스명;

        SearchRequest searchRequest = new SearchRequest(지라인덱스);
        searchRequest.source(sourceBuilder);

        try {
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] searchHits = searchResponse.getHits().getHits();

            List<지라이슈> 결과 = Optional.ofNullable(searchHits) // null 검사
                    .map(Arrays::stream)
                    .orElseGet(Stream::empty) // null인 경우 빈 스트림 반환
                    .map(SearchHit::getSourceAsString) // getSourceAsString 메서드를 사용하여 JSON 문자열을 가져옴
                    .filter(json -> json != null && !json.isEmpty()) // null이 아니고, 내용이 있는 경우만 처리
                    .map(json -> {
                        try {
                            return objectMapper.readValue(json, 지라이슈.class); // JSON 문자열을 원하는 클래스로 변환
                        } catch (JsonProcessingException e) {
                            로그.error("지라이슈 파싱 오류 : " + e.getMessage());
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
            전체결과.addAll(결과);

        } catch (IOException e) {
            로그.error("제품서비스_버전목록으로_조회 오류 : " + e.getMessage());
            throw new RuntimeException(e);
        }


        return 전체결과;

    }
}
