package com.arms.api.engine.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.arms.api.engine.models.dashboard.resource.AssigneeData;
import com.arms.api.engine.services.dashboard.common.ElasticSearchQueryHelper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.SearchHits;
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

        SearchSourceBuilder 검색조건 = new SearchSourceBuilder();

        검색조건.query(복합조회);
        검색조건.size(0); // 모든 검색 결과를 가져오기 위함

        검색조건.aggregation(
                AggregationBuilders.terms("담당자별_집계").field("assignee.assignee_displayName.keyword")
        );

        SearchRequest 검색요청 = new SearchRequest();
        검색요청.indices("jiraissue"); //인덱스 공간에서 해당 인덱스 설정
        검색요청.source(검색조건);

        // 요구사항 vs 연결된이슈&서브테스크 구분안하고 한번에
        SearchResponse 검색결과 = 지라이슈저장소.search(검색요청, RequestOptions.DEFAULT);
        Long 결과 = Optional.ofNullable(검색결과)
                .map(SearchResponse::getHits)
                .map(org.elasticsearch.search.SearchHits::getTotalHits)
                .map(totalHits -> totalHits.value)
                .orElse(0L);
        로그.info("검색결과 개수: " + 결과);

        Terms 담당자별_집계 = 검색결과.getAggregations().get("담당자별_집계");

        long 담당자_총합 = 0;
        Map<String, Long> 제품서비스별_하위이슈_담당자_집계 = new HashMap<>();
        for (Terms.Bucket 담당자 : 담당자별_집계.getBuckets()) {
            String 담당자_이메일 = 담당자.getKeyAsString();
            long 개수 = 담당자.getDocCount();
            log.info("담당자: " + 담당자_이메일 + ", Count: " + 개수);
            담당자_총합 += 개수;
            제품서비스별_하위이슈_담당자_집계.put(담당자_이메일, 개수);
        }
        제품서비스별_하위이슈_담당자_집계.put("담당자 미지정", 결과 - 담당자_총합);

        return 제품서비스별_하위이슈_담당자_집계;
    }


    @Override
    public Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별집계(Long pdServiceLink) throws IOException {

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        MatchQueryBuilder 제품아이디별_조회 = QueryBuilders.matchQuery("pdServiceId", pdServiceLink);

        sourceBuilder.query(제품아이디별_조회);

        TermsAggregationBuilder 상태별_집계 = AggregationBuilders.terms("상태별_집계").field("status.status_name.keyword");

        TermsAggregationBuilder 요구사항_여부별_집계 = AggregationBuilders.terms("요구사항_여부별_집계").field("isReq")
                .subAggregation(상태별_집계);

        TermsAggregationBuilder 담당자별_집계 = AggregationBuilders.terms("담당자별_집계").field("assignee.assignee_emailAddress.keyword")
                .subAggregation(요구사항_여부별_집계);

        sourceBuilder.aggregation(담당자별_집계);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("jiraissue");
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = 지라이슈저장소.search(searchRequest, RequestOptions.DEFAULT);

        Terms 종합집계결과 = searchResponse.getAggregations().get("담당자별_집계");

        Map<String, Map<String, Map<String, Integer>>> 담당자별_요구사항여부별_상태값_집계 = 종합집계결과.getBuckets().stream().collect(Collectors.toMap(
                Terms.Bucket::getKeyAsString,
                담당자 -> {
                    Terms 요구사항_여부별집계 = 담당자.getAggregations().get("요구사항_여부별_집계");
                    return 요구사항_여부별집계.getBuckets().stream().collect(Collectors.toMap(
                            bucket -> {
                                String 여부 = bucket.getKeyAsString();
                                if (여부.equals("true")) {
                                    return "requirement";
                                } else {
                                    return "relation_issue";
                                }
                            },
                            bucket -> {
                                Terms 상태별집계 = bucket.getAggregations().get("상태별_집계");
                                return 상태별집계.getBuckets().stream()
                                        .collect(Collectors.toMap(Terms.Bucket::getKeyAsString,
                                                상태 -> (int) 상태.getDocCount()));
                            }
                    ));
                }
        ));

        return 담당자별_요구사항여부별_상태값_집계;
    }


    @Override
    public 검색결과_목록_메인 집계결과_가져오기(쿼리_추상_팩토리 쿼리추상팩토리) throws IOException {

        SearchHits searchHits = 지라이슈저장소.operationSearch(
                쿼리추상팩토리.생성()
        );

        return new 검색결과_목록_메인(searchHits);
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

        SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource().query(boolQuery).aggregation(assigneesAgg);

        SearchResponse searchResponse = 지라이슈저장소.search(es.getSearchRequest(sourceBuilder), RequestOptions.DEFAULT);
        Terms terms = searchResponse.getAggregations().get("assignee");
        return terms.getBuckets().stream()
                .map(this::mapToAssigneeData)
                .sorted((a1, a2) -> Long.compare(a2.getIssues(), a1.getIssues()))
                .collect(Collectors.toList());
    }

    private Map<String, Object> createAssigneeData(Terms.Bucket bucket) {
        Map<String, Object> assigneeData = new HashMap<>();
        assigneeData.put("requirements", getDocCount(bucket, "requirements"));
        assigneeData.put("issues", getDocCount(bucket, "issues"));
        assigneeData.put("displayName", getFirstTermKey(bucket, "displayNames"));
        assigneeData.put("issueTypes", getTermCounts(bucket, "issueTypes"));
        assigneeData.put("priorities", getTermCounts(bucket, "priorities"));
        assigneeData.put("statuses", getTermCounts(bucket, "statuses"));
        assigneeData.put("resolutions", getTermCounts(bucket, "resolutions"));
        return Map.of(bucket.getKeyAsString(), assigneeData);
    }

    private long getDocCount(Terms.Bucket bucket, String aggName) {
        ParsedFilter filter = bucket.getAggregations().get(aggName);
        return filter.getDocCount();
    }

    private String getFirstTermKey(Terms.Bucket bucket, String aggName) {
        Terms terms = bucket.getAggregations().get(aggName);
        return terms.getBuckets().isEmpty() ? "" : terms.getBuckets().get(0).getKeyAsString();
    }

    private Map<String, Long> getTermCounts(Terms.Bucket bucket, String aggName) {
        Terms terms = bucket.getAggregations().get(aggName);
        return terms.getBuckets().stream()
                .collect(Collectors.toMap(
                        Terms.Bucket::getKeyAsString,
                        Terms.Bucket::getDocCount
                ));
    }


    private AssigneeData mapToAssigneeData(Terms.Bucket bucket) {
        AssigneeData assigneeData = new AssigneeData();
        assigneeData.setRequirements(getDocCount(bucket, "requirements"));
        assigneeData.setIssues(getDocCount(bucket, "issues"));
        assigneeData.setDisplayName(getFirstTermKey(bucket, "displayNames"));
        assigneeData.setIssueTypes(getTermCounts(bucket, "issueTypes"));
        assigneeData.setPriorities(getTermCounts(bucket, "priorities"));
        assigneeData.setStatuses(getTermCounts(bucket, "statuses"));
        assigneeData.setResolutions(getTermCounts(bucket, "resolutions"));
        return assigneeData;
    }
}
