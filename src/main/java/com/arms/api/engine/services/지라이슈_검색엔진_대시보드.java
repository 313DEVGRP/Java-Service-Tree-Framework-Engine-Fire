package com.arms.api.engine.services;

import com.arms.api.engine.dtos.*;
import com.arms.api.engine.models.*;
import com.arms.api.engine.repositories.지라이슈_저장소;
import com.arms.api.engine.vo.제품_서비스_버전;
import com.arms.api.engine.vo.하위_이슈_사항;
import com.arms.api.engine.vo.하위_이슈_사항들;
import com.arms.elasticsearch.helper.인덱스자료;
import com.arms.elasticsearch.util.aggregation.CustomAbstractAggregationBuilder;
import com.arms.elasticsearch.util.aggregation.CustomDateHistogramAggregationBuilder;
import com.arms.elasticsearch.util.aggregation.CustomTermsAggregationBuilder;
import com.arms.elasticsearch.util.query.EsQuery;
import com.arms.elasticsearch.util.query.EsQueryBuilder;
import com.arms.elasticsearch.util.query.bool.*;
import com.arms.elasticsearch.util.query.일반_집계_요청;
import com.arms.elasticsearch.util.query.쿼리_추상_팩토리;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색결과_목록_메인;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service("지라이슈_대시보드_서비스")
@AllArgsConstructor
public class 지라이슈_검색엔진_대시보드 implements 지라이슈_대시보드_서비스 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라이슈_저장소 지라이슈저장소;
    @Autowired
    private com.arms.elasticsearch.helper.인덱스_유틸 인덱스_유틸;

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
                                                                        a -> a.get필드명(),
                                                                        a -> (int) a.get개수()));
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
    public List<지라이슈> 지라이슈_조회(쿼리_추상_팩토리 쿼리추상팩토리) {
        return 지라이슈저장소.normalSearch(
                쿼리추상팩토리.생성()
        );
    }


    @Override
    public List<검색결과> 제품_버전별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청) {
        EsQuery esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersion", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
                        new ExistsQueryFilter("assignee")
                );

        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

        CustomAbstractAggregationBuilder versionsAgg = new CustomTermsAggregationBuilder("versions")
                .field("pdServiceVersion")
                .addSubAggregation(
                        new CustomTermsAggregationBuilder("assignees")
                                .field("assignee.assignee_accountId.keyword")
                                .order(BucketOrder.count(false))
                                .size(지라이슈_제품_및_제품버전_집계_요청.get크기())
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
    public List<제품_서비스_버전> 요구사항_별_상태_및_관여_작업자수_내용(검색결과_목록_메인 요구사항, 검색결과_목록_메인 하위이슈) {

        List<검색결과> pdServiceVersions = 요구사항.get검색결과().entrySet().stream().flatMap(a->a.getValue().stream()).collect(toList());
        List<검색결과> parentReqKeys = 하위이슈.get검색결과().entrySet().stream().flatMap(a->a.getValue().stream()).collect(toList());

        List<하위_이슈_사항> 하위_이슈_사항들 = parentReqKeys.stream()
                .map(issue -> new 하위_이슈_사항(issue)).collect(toList());

        return pdServiceVersions.stream()
                .map(request -> new 제품_서비스_버전(request,new 하위_이슈_사항들(하위_이슈_사항들))).collect(toList());

    }


    @Override
    public List<Worker> 작업자_별_요구사항_별_관여도(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청) {
        Map<String, Worker> contributionMap = new HashMap<>();

        List<지라이슈> requirementIssues = 지라이슈저장소.findByIsReqAndPdServiceIdAndPdServiceVersionIn(true, 지라이슈_제품_및_제품버전_집계_요청.getPdServiceLink(), 지라이슈_제품_및_제품버전_집계_요청.getPdServiceVersionLinks());

        // 요구사항의 키를 모두 추출
        List<String> allReqKeys = requirementIssues.stream().map(지라이슈::getKey).collect(Collectors.toList());

        // 모든 하위 태스크를 한 번에 로드
        List<지라이슈> allSubTasks = 지라이슈저장소.findByParentReqKeyIn(allReqKeys);

        // 하위 태스크를 부모 키로 그룹화
        Map<String, List<지라이슈>> subTasksByParent = allSubTasks.stream()
                .filter(subtask -> subtask.getAssignee() != null)
                .collect(Collectors.groupingBy(지라이슈::getParentReqKey));

        requirementIssues.stream().forEach(reqIssue -> {
            String key = reqIssue.getKey();
            String summary = reqIssue.getSummary();

            Optional.ofNullable(subTasksByParent.get(key)).orElse(Collections.emptyList()).stream().forEach(subtask -> {
                String assigneeId = subtask.getAssignee().getAccountId();
                String displayName = subtask.getAssignee().getDisplayName();

                Worker worker = contributionMap.computeIfAbsent(assigneeId, id -> {
                    Map<String, Integer> dataMap = new HashMap<>();
                    dataMap.put("totalInvolvedCount", 0);
                    return new Worker(assigneeId, displayName, dataMap, new ArrayList<>());
                });

                TaskList taskList = worker.getChildren().stream()
                        .filter(task -> task.getId().equals(key))
                        .findFirst()
                        .orElseGet(() -> {
                            Map<String, Integer> dataList = new HashMap<>();
                            dataList.put("involvedCount", 0);
                            TaskList newTask = new TaskList(key, summary, dataList);
                            worker.getChildren().add(newTask);
                            return newTask;
                        });

                taskList.getData().put("involvedCount", taskList.getData().get("involvedCount") + 1);
                worker.getData().put("totalInvolvedCount", worker.getData().get("totalInvolvedCount") + 1);
            });
        });

        return contributionMap.values().stream()
                .sorted((w1, w2) -> w2.getData().get("totalInvolvedCount").compareTo(w1.getData().get("totalInvolvedCount")))
                .limit(지라이슈_제품_및_제품버전_집계_요청.get크기() > 0 ? 지라이슈_제품_및_제품버전_집계_요청.get크기() : Long.MAX_VALUE)
                .collect(Collectors.toList());

    }

    @Override
    public Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항_지라이슈상태_주별_집계(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청) {
        LocalDate now = LocalDate.now(ZoneId.of("UTC"));
        LocalDate monthAgo = now.minusWeeks(4);

        // 1. 검색 범위에 포함되지 않은 누적 데이터를 가져온다. 기간이 길어지면 길어질수록 무의미한, 반복적인 연산이기 때문에 캐싱 고려
        요구사항_지라이슈상태_주별_집계 HistoricalData = 누적데이터조회(
                지라이슈_제품_및_제품버전_집계_요청.getPdServiceLink(),
                지라이슈_제품_및_제품버전_집계_요청.getPdServiceVersionLinks(),
                monthAgo
        );
        Map<String, Long> totalStatuses = new HashMap<>(HistoricalData.getStatuses());
        long totalIssues = HistoricalData.getTotalIssues();
        long totalRequirements = HistoricalData.getTotalRequirements();

        // 2. 검색 범위 내의 데이터를 가져온다. 현재 검색 범위는 차트 UI를 고려하여, 4~5주 정도로 적용
        EsQuery esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersion", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
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
    public Map<String, 일자별_요구사항_연결된이슈_생성개수_및_상태데이터> 지라이슈_기준일자별_제품_및_제품버전_집계검색(지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청) {

         String 시작일 = 지라이슈_일자별_제품_및_제품버전_집계_요청.get시작일();
         String 종료일 = 지라이슈_일자별_제품_및_제품버전_집계_요청.get종료일();

         String from = 시작일;
         String to = 종료일;

         EsBoolQuery[] esBoolQueries = Stream.of(
                new TermQueryMust("pdServiceId", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceLink()),
                new TermsQueryFilter("pdServiceVersion", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.REQUIREMENT ? new TermQueryMust("isReq", true) : null,
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.ISSUE ? new TermQueryMust("isReq", false) : null,
                new RangeQueryFilter(지라이슈_일자별_제품_및_제품버전_집계_요청.get일자기준(), from, to, "fromto")
         ).filter(Objects::nonNull).toArray(EsBoolQuery[]::new);

         EsQueryBuilder esQuery = new EsQueryBuilder().bool(esBoolQueries);
         BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
         });

         CustomAbstractAggregationBuilder dailyAggregationBuilder = new CustomDateHistogramAggregationBuilder("aggregation_by_day")
                 .field(지라이슈_일자별_제품_및_제품버전_집계_요청.get일자기준())
                 .calendarInterval(DateHistogramInterval.DAY);

         if (지라이슈_일자별_제품_및_제품버전_집계_요청.get메인그룹필드() != null) {
             CustomTermsAggregationBuilder 요구사항여부Aggregation = new CustomTermsAggregationBuilder("요구사항여부")
                     .field(지라이슈_일자별_제품_및_제품버전_집계_요청.get메인그룹필드());

             if (지라이슈_일자별_제품_및_제품버전_집계_요청.get하위그룹필드들() != null && 지라이슈_일자별_제품_및_제품버전_집계_요청.get하위그룹필드들().size() == 1) {
                 요구사항여부Aggregation.addSubAggregation(new CustomTermsAggregationBuilder("상태목록")
                         .field(지라이슈_일자별_제품_및_제품버전_집계_요청.get하위그룹필드들().get(0)).build());
             }

             dailyAggregationBuilder.addSubAggregation(요구사항여부Aggregation.build());
         }

         NativeSearchQueryBuilder nativeSearchQueryBuilder
                 = new NativeSearchQueryBuilder().withQuery(boolQuery).withAggregations(dailyAggregationBuilder.build());

         검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());

         List<검색결과> aggregationByDay = 검색결과_목록_메인.get검색결과().get("aggregation_by_day");

         Map<String, 일자별_요구사항_연결된이슈_생성개수_및_상태데이터> 검색결과 = aggregationByDay.stream()
                 .sorted(Comparator.comparing(bucket -> OffsetDateTime.parse(bucket.get필드명()).toLocalDate()))
                 .collect(Collectors.toMap(
                         entry -> transformDate(entry.get필드명()),
                         this::일별_생성개수_및_상태_데이터생성,
                         (existingValue, newValue) -> existingValue,
                         LinkedHashMap::new
                 ));

         return 검색결과;
     }
    @Override
    public List<지라이슈> 지라이슈_기준일자별_제품_및_제품버전_업데이트된_이슈조회(지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청){

        String 시작일 = 지라이슈_일자별_제품_및_제품버전_집계_요청.get시작일();
        String 종료일 = 지라이슈_일자별_제품_및_제품버전_집계_요청.get종료일();

        String from = 시작일;
        String to = 종료일;

        EsBoolQuery[] esBoolQueries = Stream.of(
                new TermQueryMust("pdServiceId", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceLink()),
                new TermsQueryFilter("pdServiceVersion", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.REQUIREMENT ? new TermQueryMust("isReq", true) : null,
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.ISSUE ? new TermQueryMust("isReq", false) : null,
                new RangeQueryFilter(지라이슈_일자별_제품_및_제품버전_집계_요청.get일자기준(), from, to, "fromto")
        ).filter(Objects::nonNull).toArray(EsBoolQuery[]::new);

        EsQueryBuilder esQuery = new EsQueryBuilder().bool(esBoolQueries);
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(SortBuilders.fieldSort(지라이슈_일자별_제품_및_제품버전_집계_요청.get일자기준()).order(SortOrder.ASC))
                .withMaxResults(10000);

        List<지라이슈> 전체결과 = 지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build());
        return 전체결과;
    }
    private 일자별_요구사항_연결된이슈_생성개수_및_상태데이터 일별_생성개수_및_상태_데이터생성(검색결과 결과) {
        Map<String, Long> 요구사항여부결과 = new HashMap<>();
        Map<String, Map<String, Long>> 상태목록결과 = new HashMap<>();

        결과.get하위검색결과().get("요구사항여부").forEach(term -> {
            String 필드명 = term.get필드명();
            Long 개수 = term.get개수();

            요구사항여부결과.put(필드명, 개수);

            List<검색결과> 상태목록 = Optional.ofNullable(term.get하위검색결과().get("상태목록")).orElse(Collections.emptyList());

            Map<String, Long> status = 상태목록.stream()
                    .collect(Collectors.toMap(검색결과::get필드명, 검색결과::get개수, Long::sum));

            if(status != null) {
                상태목록결과.put(필드명, status);
            }
        });

        long 요구사항_개수 = 요구사항여부결과.getOrDefault("true", 0L);
        long 연결된이슈_개수 = 요구사항여부결과.getOrDefault("false", 0L);

        Map<String, Long> 요구사항_상태목록 = 상태목록결과.getOrDefault("true", null);
        Map<String, Long> 연결된이슈_상태목록 = 상태목록결과.getOrDefault("false", null);

        return new 일자별_요구사항_연결된이슈_생성개수_및_상태데이터(요구사항_개수, 요구사항_상태목록, 연결된이슈_개수, 연결된이슈_상태목록);
    }

    @Override
    public Map<Long, Map<String, Map<String,List<요구사항_별_업데이트_데이터>>>>  요구사항별_업데이트_능선_데이터(지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청){
        String 시작일 = 지라이슈_일자별_제품_및_제품버전_집계_요청.get시작일();
        String 종료일 = 지라이슈_일자별_제품_및_제품버전_집계_요청.get종료일();

        String from = 시작일;
        String to = 종료일;

        EsBoolQuery[] esBoolQueries = Stream.of(
                new TermQueryMust("pdServiceId", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceLink()),
                new TermsQueryFilter("pdServiceVersion", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.REQUIREMENT ? new TermQueryMust("isReq", true) : null,
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.ISSUE ? new TermQueryMust("isReq", false) : null,
                new RangeQueryFilter(지라이슈_일자별_제품_및_제품버전_집계_요청.get일자기준(), from, to, "fromto")
        ).filter(Objects::nonNull).toArray(EsBoolQuery[]::new);

        EsQueryBuilder esQuery = new EsQueryBuilder().bool(esBoolQueries);
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(SortBuilders.fieldSort(지라이슈_일자별_제품_및_제품버전_집계_요청.get일자기준()).order(SortOrder.ASC))
                .withMaxResults(10000);

        List<지라이슈> 전체결과 = new ArrayList<>();

       /* DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String 지라인덱스 = 인덱스자료.지라이슈_인덱스명;

        LocalDate start = LocalDate.parse(from);
        LocalDate end = LocalDate.parse(to);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String 호출할_지라인덱스 = 지라인덱스 + "-" + date.format(formatter);
            if (!인덱스_유틸.인덱스_존재_확인(호출할_지라인덱스)) {
                continue;
            }

            List<지라이슈> 결과 = 지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build(), 호출할_지라인덱스);
            if (결과 != null && 결과.size() > 0) {
                전체결과.addAll(결과);
            }
        }*/
        boolean 인덱스존재시까지  = true;
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String 지라인덱스 = 인덱스자료.지라이슈_인덱스명;

        while(인덱스존재시까지) {
            LocalDate 오늘일경우 = LocalDate.now();
            String 호출할_지라인덱스 = 오늘일경우.format(formatter).equals(today.format(formatter))
                    ? 지라인덱스 : 지라인덱스 + "-" + today.format(formatter);

            if (!인덱스_유틸.인덱스_존재_확인(호출할_지라인덱스)) {
                인덱스존재시까지 = false;
                break;
            }

            today = today.minusDays(1);

            List<지라이슈> 결과 = 지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build(), 호출할_지라인덱스);

            if (결과 != null && 결과.size() > 0) {
                전체결과.addAll(결과);
            }
        }
        // 업데이트가 기준일에 일어난 모든 이슈를 조회
        Map<Long, Map<String, Map<String,List<요구사항_별_업데이트_데이터>>>> 조회_결과 = null;


        if (지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.ISSUE ) {

            조회_결과= 전체결과.stream()
                    .map(this::요구사항_별_업데이트_데이터)
                    .distinct()
                    .collect(Collectors.groupingBy(요구사항_별_업데이트_데이터::getPdServiceVersion,
                            Collectors.groupingBy(이슈 -> transformDateForUpdatedField(이슈.getUpdated()),
                                    Collectors.groupingBy(요구사항_별_업데이트_데이터::getParentReqKey))));

        }else if(지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.REQUIREMENT){

            조회_결과= 전체결과.stream()
                    .map(this::요구사항_별_업데이트_데이터)
                    .distinct()
                    .collect(Collectors.groupingBy(요구사항_별_업데이트_데이터::getPdServiceVersion,
                            Collectors.groupingBy(이슈 -> transformDateForUpdatedField(이슈.getUpdated()),
                                    Collectors.groupingBy(요구사항_별_업데이트_데이터::getKey))));
        }
        return 조회_결과;

    }

    private 요구사항_별_업데이트_데이터 요구사항_별_업데이트_데이터(지라이슈 issue) {
        요구사항_별_업데이트_데이터 요구사항_별_업데이트_데이터 = new 요구사항_별_업데이트_데이터();
        요구사항_별_업데이트_데이터.setKey(issue.getKey());
        요구사항_별_업데이트_데이터.setParentReqKey(issue.getParentReqKey());
        요구사항_별_업데이트_데이터.setUpdated(issue.getUpdated());
        요구사항_별_업데이트_데이터.setPdServiceVersion(issue.getPdServiceVersion());
        요구사항_별_업데이트_데이터.setSummary(issue.getSummary());
        요구사항_별_업데이트_데이터.setIsReq(issue.getIsReq());
        return 요구사항_별_업데이트_데이터;
    }
    private String transformDateForUpdatedField(String date) {
        DateTimeFormatter formatter;
        if (date.contains(".")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
        }
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(date, formatter);
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(offsetDateTime);
    }


}
