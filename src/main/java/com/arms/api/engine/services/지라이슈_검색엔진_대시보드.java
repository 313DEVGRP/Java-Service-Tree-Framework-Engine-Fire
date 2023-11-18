package com.arms.api.engine.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.arms.api.engine.models.dashboard.resource.AssigneeData;
import com.arms.api.engine.services.dashboard.common.ElasticSearchQueryHelper;
import com.arms.elasticsearch.util.검색결과;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import com.arms.api.engine.repositories.지라이슈_저장소;
import com.arms.elasticsearch.util.query.쿼리_추상_팩토리;
import com.arms.elasticsearch.util.검색결과_목록_메인;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("지라이슈_대시보드_서비스")
@AllArgsConstructor
public class 지라이슈_검색엔진_대시보드 implements 지라이슈_대시보드_서비스 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());
    private final ElasticSearchQueryHelper es;
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
        BoolQueryBuilder boolQuery = es.boolQueryBuilder(pdServiceLink, pdServiceVersionLinks)
                .filter(QueryBuilders.existsQuery("assignee"));

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
// 일단주석 sevoon0909
//    private Map<String, Object> createAssigneeData(Terms.Bucket bucket) {
//        Map<String, Object> assigneeData = new HashMap<>();
//        assigneeData.put("requirements", getDocCount(bucket, "requirements"));
//        assigneeData.put("issues", getDocCount(bucket, "issues"));
//        assigneeData.put("displayName", getFirstTermKey(bucket, "displayNames"));
//        assigneeData.put("issueTypes", getTermCounts(bucket, "issueTypes"));
//        assigneeData.put("priorities", getTermCounts(bucket, "priorities"));
//        assigneeData.put("statuses", getTermCounts(bucket, "statuses"));
//        assigneeData.put("resolutions", getTermCounts(bucket, "resolutions"));
//        return Map.of(bucket.getKeyAsString(), assigneeData);
//    }
//
//    private long getDocCount(Terms.Bucket bucket, String aggName) {
//        ParsedFilter filter = bucket.getAggregations().get(aggName);
//        return filter.getDocCount();
//    }
//
//    private String getFirstTermKey(Terms.Bucket bucket, String aggName) {
//        Terms terms = bucket.getAggregations().get(aggName);
//        return terms.getBuckets().isEmpty() ? "" : terms.getBuckets().get(0).getKeyAsString();
//    }
//
//    private Map<String, Long> getTermCounts(Terms.Bucket bucket, String aggName) {
//        Terms terms = bucket.getAggregations().get(aggName);
//        return terms.getBuckets().stream()
//                .collect(Collectors.toMap(
//                        Terms.Bucket::getKeyAsString,
//                        Terms.Bucket::getDocCount
//                ));
//    }

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
}
