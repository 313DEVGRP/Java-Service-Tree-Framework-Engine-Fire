package com.arms.api.engine.service;

import com.arms.api.engine.model.dto.*;
import com.arms.api.engine.model.enums.IsReqType;
import com.arms.api.engine.model.vo.*;
import com.arms.api.engine.model.dto.제품버전목록;
import com.arms.api.engine.model.entity.지라이슈;
import com.arms.api.engine.model.dto.지라이슈_일자별_제품_및_제품버전_집계_요청;
import com.arms.api.engine.model.dto.지라이슈_제품_및_제품버전_집계_요청;
import com.arms.api.engine.model.dto.트리맵_집계_요청;
import com.arms.api.engine.repository.인덱스자료;
import com.arms.api.engine.repository.지라이슈_저장소;
import com.arms.elasticsearch.query.*;
import com.arms.elasticsearch.query.base.기본_정렬_요청;
import com.arms.elasticsearch.query.esquery.EsBoolQuery;
import com.arms.elasticsearch.query.esquery.EsQueryString;
import com.arms.elasticsearch.query.esquery.esboolquery.must.MustTermQuery;
import com.arms.elasticsearch.query.factory.일반_집계_쿼리_생성기;
import com.arms.elasticsearch.query.filter.ExistsQueryFilter;
import com.arms.elasticsearch.query.filter.RangeQueryFilter;
import com.arms.elasticsearch.query.esquery.EsQueryBuilder;
import com.arms.elasticsearch.query.filter.TermsQueryFilter;
import com.arms.elasticsearch.버킷_집계_결과;
import com.arms.elasticsearch.버킷_집계_결과_목록_합계;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service("요구사항_분석_서비스")
@AllArgsConstructor
public class 요구사항분석_서비스_프로세스 implements 요구사항_분석_서비스 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    private 지라이슈_저장소 지라이슈저장소;

    @Override
    public 버킷_집계_결과_목록_합계 전체_집계결과_가져오기(쿼리_추상_팩토리 쿼리추상팩토리) {

        return 지라이슈저장소.전체버킷집계(
                쿼리추상팩토리.생성()
        );
    }

    @Override
    public Map<String, Long> 제품서비스별_담당자_이름_통계(Long 지라서버_아이디, Long 제품서비스_아이디) {

        BoolQueryBuilder 복합조회 = QueryBuilders.boolQuery();
        if (제품서비스_아이디 != null && 제품서비스_아이디 > 9L) {
            MatchQueryBuilder 제품서비스_조회 = QueryBuilders.matchQuery("pdServiceId", 제품서비스_아이디);
            복합조회.must(제품서비스_조회);
        }

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(복합조회)
                .addAggregation(
                        AggregationBuilders
                                .terms("담당자별_집계").field("assignee.assignee_displayName.keyword")
                ).withMaxResults(0);

        // 요구사항 vs 연결된이슈&서브테스크 구분안하고 한번에
        버킷_집계_결과_목록_합계 버킷_집계_결과_목록_합계 = 지라이슈저장소.버킷집계(nativeSearchQueryBuilder.build());
        Long 결과 = 버킷_집계_결과_목록_합계.get전체합계();
        로그.info("검색결과 개수: " + 결과);

        List<버킷_집계_결과> 담당자별_집계 = 버킷_집계_결과_목록_합계.get검색결과().get("담당자별_집계");

        long 담당자_총합 = 0;
        Map<String, Long> 제품서비스별_하위이슈_담당자_집계 = new HashMap<>();
        for (버킷_집계_결과 담당자 : 담당자별_집계) {
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
                .addAggregation(담당자별_집계)
                .withMaxResults(0);

        버킷_집계_결과_목록_합계 버킷_집계_결과_목록_합계 = 지라이슈저장소.버킷집계(nativeSearchQueryBuilder.build());

        Map<String, Map<String, Map<String, Integer>>> 담당자별_요구사항여부별_상태값_집계
                = 버킷_집계_결과_목록_합계.get검색결과().get("담당자별_집계")
                .stream()
                .collect(
                        Collectors.toMap(
                                버킷_집계_결과::get필드명,
                                담당자 -> {
                                    List<버킷_집계_결과> 요구사항_여부별_집계 = 담당자.get하위검색결과().get("요구사항_여부별_집계");
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
                                                        List<버킷_집계_결과> 상태별_집계 = 검색결과.get하위검색결과().get("상태별_집계");
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
    public 버킷_집계_결과_목록_합계 집계결과_가져오기(쿼리_추상_팩토리 쿼리추상팩토리) {

        return 지라이슈저장소.버킷집계(
                쿼리추상팩토리.생성()
        );
    }

    @Override
    public List<버킷_집계_결과> 제품_버전별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청) {
        EsQuery esQuery = new EsQueryBuilder()
                .bool(new MustTermQuery("pdServiceId", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceLink()),
                        new MustTermQuery("isReq", 지라이슈_제품_및_제품버전_집계_요청.getIsReqType().isNotAllAndIsReq()),
                        new TermsQueryFilter("pdServiceVersions", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
                        new ExistsQueryFilter("assignee")
                );

        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

        TermsAggregationBuilder versionsAgg = AggregationBuilders.terms("versions").field("pdServiceVersions")
                .subAggregation(
                        AggregationBuilders.terms("assignees")
                                .field("assignee.assignee_accountId.keyword")
                                .order(BucketOrder.count(false))
                                .size(지라이슈_제품_및_제품버전_집계_요청.get하위크기())
                                .subAggregation(AggregationBuilders.terms("displayNames").field("assignee.assignee_displayName.keyword"))
                );

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .addAggregation(versionsAgg)
                .build();

        버킷_집계_결과_목록_합계 버킷_집계_결과_목록_합계 = 지라이슈저장소.버킷집계(searchQuery);

        List<버킷_집계_결과> 버전검색결과 = 버킷_집계_결과_목록_합계.get검색결과().get("versions");

        List<String> filteredVersionIds = Arrays.stream(지라이슈_제품_및_제품버전_집계_요청.getPdServiceVersionLinks())
                .map(String::valueOf)
                .collect(Collectors.toList());

        return 버전검색결과.stream().filter(버전 -> filteredVersionIds.contains(버전.get필드명())).collect(toList());
    }

    @Override
    public List<요구사항_버전_이슈_키_상태_작업자수> 버전별_요구사항_상태_및_관여_작업자수_내용(Long pdServiceLink, Long[] pdServiceVersionLinks){
        List<지라이슈> 요구사항_이슈_목록 = 지라이슈저장소.findByIsReqAndPdServiceIdAndPdServiceVersionsIn(true, pdServiceLink, pdServiceVersionLinks);
        List<지라이슈> 담당자_존재_요구사항_이슈_목록 = 요구사항_이슈_목록.stream()
                                        .filter(지라이슈 -> 지라이슈.getAssignee() != null)
                                        .collect(toList());
        log.info(String.valueOf(요구사항_이슈_목록.size()));

        // 담당자 있는 요구사항_이슈의 키만 뽑기
        List<String> 담당자_존재_요구사항_이슈_키 = 담당자_존재_요구사항_이슈_목록.stream()
                .map(지라이슈::getKey).collect(Collectors.toList());
        List<지라이슈> allSubTasks = 지라이슈저장소.findByParentReqKeyIn(담당자_존재_요구사항_이슈_키);

        //담당자가 있는 연결이슈만, 요구사항_이슈키에 매핑
        Map<String, List<지라이슈>> 요구사항이슈_담당자있는_하위이슈들 = allSubTasks.stream()
                .filter(subtask -> subtask.getAssignee() != null)
                .collect(Collectors.groupingBy(지라이슈::getParentReqKey));

        List<요구사항_버전_이슈_키_상태_작업자수> 요구사항_버전이슈키상태_작업자수_목록 = new ArrayList<>();

        // 요구사항_이슈의 작업자의 메일을 set에 담고, 하위이슈 담당자 메일을 set에 담아서
        // 그 결과 set의 크기를 해당 요구사항_이슈에 관련된 작업자수로 가져가는게 나을듯 싶음.
        for(지라이슈 요구사항_이슈 : 담당자_존재_요구사항_이슈_목록) {
            Set 메일_세트 = new HashSet();
            메일_세트.add(요구사항_이슈.getAssignee().getEmailAddress());

            String 이슈키 = 요구사항_이슈.getKey();
            if(요구사항이슈_담당자있는_하위이슈들.containsKey(이슈키)) {
                List<지라이슈> 하위이슈들 = 요구사항이슈_담당자있는_하위이슈들.get(이슈키);
                for(지라이슈 하위이슈 : 하위이슈들) {
                    메일_세트.add(하위이슈.getAssignee().getEmailAddress());
                }
            }
            요구사항_버전_이슈_키_상태_작업자수 요구사항_버전_이슈_키_상태_작업자수 = new 요구사항_버전_이슈_키_상태_작업자수();
            요구사항_버전_이슈_키_상태_작업자수.setVersionArr(요구사항_이슈.getPdServiceVersions());
            요구사항_버전_이슈_키_상태_작업자수.setIssueKey(요구사항_이슈.getKey());
            요구사항_버전_이슈_키_상태_작업자수.setStatusName(요구사항_이슈.getStatus().getName());
            요구사항_버전_이슈_키_상태_작업자수.setNumOfWorkers(메일_세트.size());

            요구사항_버전이슈키상태_작업자수_목록.add(요구사항_버전_이슈_키_상태_작업자수);
            //담당자 수 매핑이 끝난 요구사항은 Map에서 제거
            요구사항이슈_담당자있는_하위이슈들.remove(이슈키);
        }
        return 요구사항_버전이슈키상태_작업자수_목록;
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
                .bool(new MustTermQuery("pdServiceId", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersions", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
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
                = new NativeSearchQueryBuilder().withQuery(boolQuery).addAggregation(weeklyAggregationBuilder);

        버킷_집계_결과_목록_합계 버킷_집계_결과_목록_합계 = 지라이슈저장소.버킷집계(nativeSearchQueryBuilder.build());

        List<버킷_집계_결과> aggregationByWeek = 버킷_집계_결과_목록_합계.get검색결과().get("aggregation_by_week");


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

    public 요구사항_지라이슈상태_주별_집계 누적데이터조회(Long pdServiceLink, Long[] pdServiceVersionLinks, LocalDate monthAgo) {
        // 총 이슈 개수를 구하기 위한 쿼리
        EsQuery issueEsQuery = new EsQueryBuilder()
                .bool(new MustTermQuery("pdServiceId", pdServiceLink),
                        new TermsQueryFilter("pdServiceVersions", pdServiceVersionLinks),
                        new RangeQueryFilter("created", null, monthAgo, "lt")
                );
        BoolQueryBuilder boolQueryForTotalIssues = issueEsQuery.getQuery(new ParameterizedTypeReference<>() {});

        // 총 요구사항 개수를 구하기 위한 쿼리
        EsQuery reqEsQuery = new EsQueryBuilder()
                .bool(new MustTermQuery("pdServiceId", pdServiceLink),
                        new MustTermQuery("isReq", true),
                        new RangeQueryFilter("created", null, monthAgo, "lt")
                );
        BoolQueryBuilder boolQueryForTotalRequirements = reqEsQuery.getQuery(new ParameterizedTypeReference<>() {});

        // 이슈 상태에 대한 집계 쿼리
        TermsAggregationBuilder totalAggregationBuilder = AggregationBuilders.terms("total_status").field("status.status_name.keyword");

        // 총 이슈 개수 검색
        NativeSearchQueryBuilder nativeSearchQueryBuilderForTotalIssues
                = new NativeSearchQueryBuilder().withQuery(boolQueryForTotalIssues).addAggregation(totalAggregationBuilder);

        버킷_집계_결과_목록_합계 searchResponseForTotalIssues
                = 지라이슈저장소.버킷집계(nativeSearchQueryBuilderForTotalIssues.build());

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
                    for (버킷_집계_결과 버킷_집계_결과 : totalStatus) {
                        String key = 버킷_집계_결과.get필드명();
                        long value = 버킷_집계_결과.get개수();
                        statusMap.put(key, value);
                    }
                });

        return new 요구사항_지라이슈상태_주별_집계(totalIssuesCount, statusMap, totalRequirementsCount);
    }

    private 요구사항_지라이슈상태_주별_집계 주별데이터생성(버킷_집계_결과 검색_결과) {

        Map<String, Long> statuses = (검색_결과.get하위검색결과().get("statuses")).stream()
                .collect(Collectors.toMap(버킷_집계_결과::get필드명, 버킷_집계_결과::get개수));

        long totalReqCount = Optional.ofNullable(검색_결과.get하위검색결과().get("requirements"))
                .flatMap(reqs -> reqs.stream()
                        .filter(bucket -> "true".equals(bucket.get필드명()))
                        .findFirst())
                .map(버킷_집계_결과::get개수)
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
                new MustTermQuery("pdServiceId", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceLink()),
                new TermsQueryFilter("pdServiceVersions", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.REQUIREMENT ? new MustTermQuery("isReq", true) : null,
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.ISSUE ? new MustTermQuery("isReq", false) : null,
                new RangeQueryFilter(지라이슈_일자별_제품_및_제품버전_집계_요청.get일자기준(), from, to, "fromto")
         ).filter(Objects::nonNull).toArray(EsBoolQuery[]::new);

         EsQueryBuilder esQuery = new EsQueryBuilder().bool(esBoolQueries);
         BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
         });


        DateHistogramAggregationBuilder dailyAggregationBuilder = AggregationBuilders
                .dateHistogram("aggregation_by_day")
                .field(지라이슈_일자별_제품_및_제품버전_집계_요청.get일자기준())
                .calendarInterval(DateHistogramInterval.DAY);


         if (지라이슈_일자별_제품_및_제품버전_집계_요청.get메인그룹필드() != null) {
             TermsAggregationBuilder 요구사항여부Aggregation = AggregationBuilders
                     .terms("요구사항여부")
                     .field(지라이슈_일자별_제품_및_제품버전_집계_요청.get메인그룹필드());

             if (지라이슈_일자별_제품_및_제품버전_집계_요청.get하위그룹필드들() != null && 지라이슈_일자별_제품_및_제품버전_집계_요청.get하위그룹필드들().size() == 1) {
                 요구사항여부Aggregation.subAggregation(AggregationBuilders.terms("상태목록").field(지라이슈_일자별_제품_및_제품버전_집계_요청.get하위그룹필드들().get(0)));
             }

             dailyAggregationBuilder.subAggregation(요구사항여부Aggregation);
         }

         NativeSearchQueryBuilder nativeSearchQueryBuilder
                 = new NativeSearchQueryBuilder().withQuery(boolQuery).addAggregation(dailyAggregationBuilder);

         버킷_집계_결과_목록_합계 버킷_집계_결과_목록_합계 = 지라이슈저장소.버킷집계(nativeSearchQueryBuilder.build());

         List<버킷_집계_결과> aggregationByDay = 버킷_집계_결과_목록_합계.get검색결과().get("aggregation_by_day");

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
                new MustTermQuery("pdServiceId", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceLink()),
                new TermsQueryFilter("pdServiceVersions", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.REQUIREMENT ? new MustTermQuery("isReq", true) : null,
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.ISSUE ? new MustTermQuery("isReq", false) : null,
                new RangeQueryFilter(지라이슈_일자별_제품_및_제품버전_집계_요청.get일자기준(), from, to, "fromto")
        ).filter(Objects::nonNull).toArray(EsBoolQuery[]::new);

        EsQueryBuilder esQuery = new EsQueryBuilder().bool(esBoolQueries);
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSort(SortBuilders.fieldSort(지라이슈_일자별_제품_및_제품버전_집계_요청.get일자기준()).order(SortOrder.ASC))
                .withMaxResults(10000);

        return 지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build());
    }
    private 일자별_요구사항_연결된이슈_생성개수_및_상태데이터 일별_생성개수_및_상태_데이터생성(버킷_집계_결과 결과) {
        Map<String, Long> 요구사항여부결과 = new HashMap<>();
        Map<String, Map<String, Long>> 상태목록결과 = new HashMap<>();

        결과.get하위검색결과().get("요구사항여부").forEach(term -> {
            String 필드명 = term.get필드명();
            Long 개수 = term.get개수();

            요구사항여부결과.put(필드명, 개수);

            List<버킷_집계_결과> 상태목록 = Optional.ofNullable(term.get하위검색결과().get("상태목록")).orElse(Collections.emptyList());

            Map<String, Long> status = 상태목록.stream()
                    .collect(Collectors.toMap(버킷_집계_결과::get필드명, 버킷_집계_결과::get개수, Long::sum));

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
                new MustTermQuery("pdServiceId", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceLink()),
                new TermsQueryFilter("pdServiceVersions", 지라이슈_일자별_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.REQUIREMENT ? new MustTermQuery("isReq", true) : null,
                지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.ISSUE ? new MustTermQuery("isReq", false) : null,
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

        boolean 인덱스존재시까지  = true;
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String 지라인덱스 = 인덱스자료.지라이슈_인덱스명;

        while(인덱스존재시까지) {
            LocalDate 오늘일경우 = LocalDate.now();
            String 호출할_지라인덱스 = 오늘일경우.format(formatter).equals(today.format(formatter))
                    ? 지라인덱스 : 지라인덱스 + "-" + today.format(formatter);

            if (!지라이슈저장소.인덱스_존재_확인(호출할_지라인덱스)) {
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
                    .collect(toList())
                    .stream().flatMap(업데이트_데이터들->업데이트_데이터들.stream())
                    .distinct()
                    .collect(Collectors.groupingBy(요구사항_별_업데이트_데이터::getPdServiceVersion,
                            Collectors.groupingBy(이슈 -> transformDateForUpdatedField(이슈.getUpdated()),
                                    Collectors.groupingBy(요구사항_별_업데이트_데이터::getParentReqKey))));

        }else if(지라이슈_일자별_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.REQUIREMENT){

            조회_결과= 전체결과.stream()
                    .map(this::요구사항_별_업데이트_데이터)
                    .collect(toList())
                    .stream().flatMap(업데이트_데이터들->업데이트_데이터들.stream())
                    .distinct()
                    .collect(Collectors.groupingBy(요구사항_별_업데이트_데이터::getPdServiceVersion,
                            Collectors.groupingBy(이슈 -> transformDateForUpdatedField(이슈.getUpdated()),
                                    Collectors.groupingBy(요구사항_별_업데이트_데이터::getKey))));
        }
        return 조회_결과;

    }

    private List<요구사항_별_업데이트_데이터> 요구사항_별_업데이트_데이터(지라이슈 issue) {

        return Arrays.stream(issue.getPdServiceVersions()).collect(toList())
            .stream()
            .map(지라이슈->{
                요구사항_별_업데이트_데이터 요구사항_별_업데이트_데이터 = new 요구사항_별_업데이트_데이터();
                요구사항_별_업데이트_데이터.setKey(issue.getKey());
                요구사항_별_업데이트_데이터.setParentReqKey(issue.getParentReqKey());
                요구사항_별_업데이트_데이터.setUpdated(issue.getUpdated());
                요구사항_별_업데이트_데이터.setPdServiceVersion(지라이슈.longValue());
                요구사항_별_업데이트_데이터.setSummary(issue.getSummary());
                요구사항_별_업데이트_데이터.setIsReq(issue.getIsReq());
                return 요구사항_별_업데이트_데이터;
            }).collect(toList());
    }
    private String transformDateForUpdatedField(String date) {
        String subDate = date.substring(0,10);
        LocalDate localDate = LocalDate.parse(subDate);
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(localDate);
    }

    @Override
    public List<버킷_집계_결과> 제품_버전별_요구사항별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청) {

        boolean 요구사항여부 = false;
        if (지라이슈_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.REQUIREMENT) {
            요구사항여부 = true;
        }
        else if (지라이슈_제품_및_제품버전_집계_요청.getIsReqType() == IsReqType.ISSUE) {
            요구사항여부 = false;
        }

        EsQuery esQuery = new EsQueryBuilder()
                .bool(new MustTermQuery("pdServiceId", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceLink()),
                        new MustTermQuery("isReq", 요구사항여부),
                        new TermsQueryFilter("pdServiceVersions", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
                        new ExistsQueryFilter("assignee")
                );

        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

        AggregationBuilder subAggregation;
        if (요구사항여부) {
            subAggregation = AggregationBuilders
                    .terms("requirement")
                    .field("key")
                    .size(지라이슈_제품_및_제품버전_집계_요청.get크기())
                    .subAggregation(
                            AggregationBuilders.terms("assignees")
                                    .field("assignee.assignee_accountId.keyword")
                                    .order(BucketOrder.count(false))
                                    .size(지라이슈_제품_및_제품버전_집계_요청.get크기())
                                    .subAggregation(AggregationBuilders.terms("displayNames").field("assignee.assignee_displayName.keyword"))
                    );

        } else {
            subAggregation = AggregationBuilders
                    .terms("parentRequirement")
                    .field("parentReqKey")
                    .size(지라이슈_제품_및_제품버전_집계_요청.get크기())
                    .subAggregation(AggregationBuilders.terms("assignees")
                            .field("assignee.assignee_accountId.keyword")
                            .order(BucketOrder.count(false))
                            .size(지라이슈_제품_및_제품버전_집계_요청.get크기())
                            .subAggregation(AggregationBuilders.terms("displayNames").field("assignee.assignee_displayName.keyword"))
                    );
        }


        TermsAggregationBuilder versionsAgg = AggregationBuilders.terms("versions").field("pdServiceVersions").subAggregation(subAggregation);


        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .addAggregation(versionsAgg)
                .build();

        버킷_집계_결과_목록_합계 버킷_집계_결과_목록_합계 = 지라이슈저장소.버킷집계(searchQuery);

        return 버킷_집계_결과_목록_합계.get검색결과().get("versions");

    }

    @Override
    public Map<String,List<요구사항_지라이슈키별_업데이트_목록_데이터>> 요구사항_지라이슈키별_업데이트_목록(List<String> 지라키_목록){

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        for (String 지라키 : 지라키_목록) {
            boolQuery.should(QueryBuilders.termQuery("parentReqKey", 지라키));
            boolQuery.should(QueryBuilders.termQuery("key", 지라키));
        }

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withMaxResults(10000);

        List<지라이슈> 전체결과 = new ArrayList<>();

        boolean 인덱스존재시까지  = true;
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String 지라인덱스 = 인덱스자료.지라이슈_인덱스명;

        while(인덱스존재시까지) {
            LocalDate 오늘일경우 = LocalDate.now();
            String 호출할_지라인덱스 = 오늘일경우.format(formatter).equals(today.format(formatter))
                    ? 지라인덱스 : 지라인덱스 + "-" + today.format(formatter);

            if (!지라이슈저장소.인덱스_존재_확인(호출할_지라인덱스)) {
                인덱스존재시까지 = false;
                break;
            }

            today = today.minusDays(1);

            List<지라이슈> 결과 = 지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build(), 호출할_지라인덱스);

            if (결과 != null && 결과.size() > 0) {
                전체결과.addAll(결과);
            }
        }

        Map<String, List<요구사항_지라이슈키별_업데이트_목록_데이터>> 조회_결과 = 전체결과.stream()
                .map(this::요구사항_지라이슈키별_업데이트_목록_데이터)
                .distinct()
                .collect(Collectors.groupingBy(data -> data.getIsReq() ? data.getKey() : data.getParentReqKey()));

        // 하위 이슈로 작업한 경우 요구사항 데이터는 제거
        조회_결과.forEach((key, valueList) -> {
            if (valueList.stream().anyMatch(data -> data.getIsReq() == false)) {
                valueList.removeIf(data -> data.getIsReq() == true);
            }
        });

        return 조회_결과;
    }

    private 요구사항_지라이슈키별_업데이트_목록_데이터 요구사항_지라이슈키별_업데이트_목록_데이터(지라이슈 지라이슈){
        return new 요구사항_지라이슈키별_업데이트_목록_데이터(
                지라이슈.getKey(),
                지라이슈.getParentReqKey(),
                지라이슈.getUpdated(),
                지라이슈.getResolutiondate(),
                지라이슈.getIsReq()
        );

    }

    @Override
    public List<지라이슈> 제품서비스_버전목록으로_조회(Long pdServiceLink, Long[] pdServiceVersionLinks) {
        return 지라이슈저장소.findByPdServiceIdAndPdServiceVersionsIn(pdServiceLink, pdServiceVersionLinks);
    }

    @Override
    public 히트맵데이터 히트맵_제품서비스_버전목록으로_조회(Long pdServiceLink, Long[] pdServiceVersionLinks) {

        EsQuery esQuery = new EsQueryBuilder()
                .bool(new MustTermQuery("pdServiceId", pdServiceLink),
                        new TermsQueryFilter("pdServiceVersions", pdServiceVersionLinks)
                );
        BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {
        });

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(0, 10000));

        List<지라이슈> 전체결과 = new ArrayList<>();
        boolean 인덱스존재시까지  = true;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String 지라인덱스 = 인덱스자료.지라이슈_인덱스명;

        while(인덱스존재시까지) {
            LocalDate 오늘일경우 = LocalDate.now();
            String 호출할_지라인덱스 = 오늘일경우.format(formatter).equals(today.format(formatter))
                    ? 지라인덱스 : 지라인덱스 + "-" + today.format(formatter);

            if (!지라이슈저장소.인덱스_존재_확인(호출할_지라인덱스)) {
                인덱스존재시까지 = false;
                break;
            }

            today = today.minusDays(1);

            List<지라이슈> 결과 = 지라이슈저장소.normalSearch(nativeSearchQueryBuilder.build(), 호출할_지라인덱스);

            if (결과 != null && 결과.size() > 0) {
                전체결과.addAll(결과);
            }
        }

        히트맵데이터 히트맵데이터 = new 히트맵데이터();
        Set<String> requirementColors = new HashSet<>();
        Set<String> relationIssueColors = new HashSet<>();

        전체결과.stream().forEach(지라이슈 -> {
            if (지라이슈.getIsReq()) {
                히트맵데이터_파싱(히트맵데이터.getRequirement(), 지라이슈, requirementColors);
            } else {
                히트맵데이터_파싱(히트맵데이터.getRelationIssue(), 지라이슈, relationIssueColors);
            }
        });

        히트맵데이터.setRequirementColors(assignColors(requirementColors));
        히트맵데이터.setRelationIssueColors(assignColors(relationIssueColors));

        return 히트맵데이터;
    }

    private void 히트맵데이터_파싱(Map<String, 히트맵날짜데이터> returnObject, 지라이슈 item, Set<String> returnColors) {
        if (item.getUpdated() == null || item.getUpdated().isEmpty()) {
            로그.info(item.getKey());
            return;
        }

        String 표시날짜 = formatDate(parseDateTime(item.getUpdated()));

        if (!returnObject.containsKey(표시날짜)) {
            returnObject.put(표시날짜, new 히트맵날짜데이터());
        }

        히트맵날짜데이터 히트맵날짜데이터 = returnObject.get(표시날짜);
        히트맵날짜데이터.getContents().add(item.getSummary());
        히트맵날짜데이터.setCount(returnObject.get(표시날짜).getContents().size());
        히트맵날짜데이터.setItems(Collections.singleton(히트맵날짜데이터.getCount() + "개 업데이트"));
        returnColors.add(item.getSummary());
    }

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return format.format(date);
    }

    private Map<String, String> assignColors(Set<String> colorsArray) {
        Map<String, String> colorsObj = new HashMap<>();
        colorsObj.put("default", "#eeeeee");

        colorsArray.forEach(item -> {
            if (!"default".equals(item)) {
                colorsObj.put(item, getRandomColor());
            }
        });

        return colorsObj;
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {

        try {
            // 온프레미스 날짜형식
            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            // 클라우드 날짜형식
            DateTimeFormatter formatterWithoutColonInTimeZone =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            return LocalDateTime.parse(dateTimeStr, formatterWithoutColonInTimeZone);
        }
    }

    private String getRandomColor() {
        SecureRandom random = null;

        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            로그.error("랜덤컬러 데이터 생성중 오류 : " + e.getMessage());
            throw new RuntimeException(e);
        }

        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();

        Color randomColor = new Color(r, g, b);

        return "#" + Integer.toHexString(randomColor.getRGB()).substring(2);
    }

}
