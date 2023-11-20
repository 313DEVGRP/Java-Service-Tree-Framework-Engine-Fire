package com.arms.api.engine.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.arms.api.engine.models.dashboard.bar.요구사항_지라이슈상태_주별_집계;
import com.arms.api.engine.models.dashboard.sankey.SankeyElasticSearchData;
import com.arms.api.engine.models.dashboard.treemap.TaskList;
import com.arms.api.engine.models.dashboard.treemap.Worker;
import com.arms.api.engine.models.지라이슈;
import com.arms.elasticsearch.util.query.*;
import com.arms.elasticsearch.util.query.bool.ExistsQueryFilter;
import com.arms.elasticsearch.util.query.bool.RangeQueryFilter;
import com.arms.elasticsearch.util.query.bool.TermQueryMust;
import com.arms.elasticsearch.util.query.bool.TermsQueryFilter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.arms.api.engine.models.dashboard.resource.AssigneeData;
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

    @Override
    public Map<String, Long> 제품서비스별_담당자_이름_통계(Long 지라서버_아이디, Long 제품서비스_아이디) throws IOException {

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
    public Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별집계(Long pdServiceLink) throws IOException {


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
    public 검색결과_목록_메인 집계결과_가져오기(쿼리_추상_팩토리 쿼리추상팩토리) throws IOException {

        return 지라이슈저장소.aggregationSearch(
            쿼리추상팩토리.생성()
        );
    }

    @Override
    public List<AssigneeData> 리소스_담당자_데이터_리스트(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException {
        EsQueryBuilder esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", pdServiceLink),
                        new TermsQueryFilter("pdServiceVersion", pdServiceVersionLinks),
                        new ExistsQueryFilter("assignee")
                );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

        FilterAggregationBuilder reqAgg = AggregationBuilders
                .filter("requirements", QueryBuilders.termQuery("isReq", true));
        FilterAggregationBuilder issueAgg = AggregationBuilders
                .filter("issues", QueryBuilders.termQuery("isReq", false));
        TermsAggregationBuilder assigneesAgg = AggregationBuilders.terms("assignee").field("assignee.assignee_accountId.keyword")
                .subAggregation(AggregationBuilders.terms("displayNames").field("assignee.assignee_displayName.keyword"))
                .subAggregation(reqAgg)
                .subAggregation(issueAgg)
                .subAggregation(AggregationBuilders.terms("issueTypes").field("issuetype.issuetype_name.keyword"))
                .subAggregation(AggregationBuilders.terms("priorities").field("priority.priority_name.keyword"))
                .subAggregation(AggregationBuilders.terms("statuses").field("status.status_name.keyword"))
                .subAggregation(AggregationBuilders.terms("resolutions").field("resolution.resolution_name.keyword"));

        NativeSearchQueryBuilder nativeSearchQueryBuilder
                = new NativeSearchQueryBuilder().withQuery(boolQuery).withAggregations(assigneesAgg);
        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());
        List<검색결과> assignee = 검색결과_목록_메인.그룹결과("assignee");

        return assignee
                .stream()
                .map(this::mapToAssigneeData)
                .sorted((a1, a2) -> Long.compare(a2.getIssues(), a1.getIssues()))
                .collect(Collectors.toList());
    }


    private AssigneeData mapToAssigneeData(검색결과 검색결과) {

        AssigneeData assigneeData = new AssigneeData();
        assigneeData.setRequirements(검색결과.필터필드개수("requirements"));
        assigneeData.setIssues(검색결과.필터필드개수("issues"));
        assigneeData.setDisplayName(검색결과.필터필드명( "displayNames"));
        assigneeData.setIssueTypes(검색결과.검색결과_맵처리("issueTypes"));
        assigneeData.setPriorities(검색결과.검색결과_맵처리( "priorities"));
        assigneeData.setStatuses(검색결과.검색결과_맵처리( "statuses"));
        assigneeData.setResolutions(검색결과.검색결과_맵처리( "resolutions"));
        return assigneeData;
    }

    @Override
    public Map<String, List<SankeyElasticSearchData>> 제품_버전별_담당자_목록(Long pdServiceLink, List<Long> pdServiceVersionLinks, int maxResults) throws IOException {
        EsQueryBuilder esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", pdServiceLink),
                        new TermsQueryFilter("pdServiceVersion", pdServiceVersionLinks),
                        new ExistsQueryFilter("assignee")
                );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

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


    @Override
    public List<Worker> 작업자_별_요구사항_별_관여도(Long pdServiceLink, List<Long> pdServiceVersionLinks, int maxResults) {
        Map<String, Worker> contributionMap = new HashMap<>();

        List<지라이슈> requirementIssues = 지라이슈저장소.findByIsReqAndPdServiceIdAndPdServiceVersionIn(true, pdServiceLink, pdServiceVersionLinks);

        // 요구사항의 키를 모두 추출
        List<String> allReqKeys = requirementIssues.stream().map(지라이슈::getKey).collect(Collectors.toList());

        // 모든 하위 태스크를 한 번에 로드
        List<지라이슈> allSubTasks = 지라이슈저장소.findByParentReqKeyIn(allReqKeys);

        // 하위 태스크를 부모 키로 그룹화
        Map<String, List<지라이슈>> subTasksByParent = allSubTasks.stream()
                .filter(subtask -> subtask.getAssignee() != null)
                .collect(Collectors.groupingBy(지라이슈::getParentReqKey));

        for (지라이슈 reqIssue : requirementIssues) {
            String key = reqIssue.getKey();
            String summary = reqIssue.getSummary();

            List<지라이슈> subTasks = subTasksByParent.getOrDefault(key, Collections.emptyList());

            for (지라이슈 subtask : subTasks) {
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
            }
        }


        if (maxResults > 0) {
            return contributionMap.values().stream()
                    .sorted((w1, w2) -> w2.getData().get("totalInvolvedCount").compareTo(w1.getData().get("totalInvolvedCount")))
                    .limit(maxResults)
                    .collect(Collectors.toList());
        }

        return contributionMap.values().stream()
                .sorted((w1, w2) -> w2.getData().get("totalInvolvedCount").compareTo(w1.getData().get("totalInvolvedCount")))
                .collect(Collectors.toList());

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
        EsQueryBuilder esQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", pdServiceLink),
                        new TermsQueryFilter("pdServiceVersion", pdServiceVersionLinks),
                        new RangeQueryFilter("created", monthAgo, now, "fromto")
                );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

        DateHistogramAggregationBuilder weeklyAggregationBuilder = AggregationBuilders
                .dateHistogram("aggregation_by_week")
                .field("created")
                .calendarInterval(DateHistogramInterval.WEEK)
                .subAggregation(AggregationBuilders.terms("statuses").field("status.status_name.keyword"))
                .subAggregation(AggregationBuilders.terms("requirements").field("isReq"));

        NativeSearchQueryBuilder nativeSearchQueryBuilder
                = new NativeSearchQueryBuilder().withQuery(boolQuery).withAggregations(weeklyAggregationBuilder);

        검색결과_목록_메인 검색결과_목록_메인 = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilder.build());

        List<검색결과> aggregationByWeek = 검색결과_목록_메인.get검색결과().get("aggregation_by_week");


        Map<String, 요구사항_지라이슈상태_주별_집계> 검색결과 = aggregationByWeek.stream()
                .sorted(Comparator.comparing(bucket -> OffsetDateTime.parse(bucket.get필드명()).toLocalDate()))
                .collect(Collectors.toMap(
                        entry ->  transformDate(entry.get필드명()),
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

    public 요구사항_지라이슈상태_주별_집계 누적데이터조회(Long pdServiceLink, List<Long> pdServiceVersionLinks, LocalDate monthAgo) throws IOException {
        // 총 이슈 개수를 구하기 위한 쿼리
        EsQueryBuilder issueEsQuery = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId", pdServiceLink),
                        new TermsQueryFilter("pdServiceVersion", pdServiceVersionLinks),
                        new RangeQueryFilter("created", null, monthAgo, "lt")
                );
        BoolQueryBuilder boolQueryForTotalIssues = issueEsQuery.getQuery(new ParameterizedTypeReference<>() {});

        // 총 요구사항 개수를 구하기 위한 쿼리
        EsQueryBuilder reqEsQuery = new EsQueryBuilder()
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

//        검색결과_목록_메인 검색결과_목록_메인
//                = 지라이슈저장소.aggregationSearch(nativeSearchQueryBuilderForTotalRequirements.build());
//        Long totalRequirementsCount = 검색결과_목록_메인.get전체합계();

        Long totalRequirementsCount = Long.valueOf(Optional.ofNullable(지라이슈저장소.normalSearch(nativeSearchQueryBuilderForTotalRequirements.build()))
                .map(지라이슈 -> 지라이슈.size())
                .orElse(0));

        // 이슈 상태 집계 결과 가져오기
        Map<String, Long> statusMap = new HashMap<>();
        Optional.ofNullable(searchResponseForTotalIssues)
                .map(response ->  response.get검색결과().get("total_status"))
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
}
