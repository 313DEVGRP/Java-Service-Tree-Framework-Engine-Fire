package com.arms.elasticsearch.util;
import com.arms.elasticsearch.helper.인덱스자료;
import com.arms.elasticsearch.models.지라이슈;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public final class 검색엔진_유틸 {

    public RestHighLevelClient getClient() {
        return client;
    }

    private final RestHighLevelClient client;


    private static final ObjectMapper MAPPER = new ObjectMapper();

    public 검색엔진_유틸(RestHighLevelClient client) {
        this.client = client;
    }




    public <T> List<T>  searchInternal(final SearchRequest request,Class<T> valueType) {
        if (request == null) {
            log.error("Failed to build search request");
            return Collections.emptyList();
        }

        try {
            final SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            final SearchHit[] searchHits = response.getHits().getHits();
            final List<T> vehicles = new ArrayList<>(searchHits.length);

            for (SearchHit hit : searchHits) {

                vehicles.add(
                        MAPPER.readValue(hit.getSourceAsString(), valueType)
                );
            }

            return vehicles;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public Map<String, Long> 특정필드의_값들을_그룹화하여_빈도수가져오기(String indexName, String groupByField) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("group_by")
                        .field(groupByField)
                        .size(100)  // Change the size as needed
        );
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        ParsedStringTerms groupByAgg = searchResponse.getAggregations().get("group_by");
        Map<String, Long> result = new HashMap<>();

        for (Terms.Bucket bucket : groupByAgg.getBuckets()) {
            String groupValue = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            result.put(groupValue, docCount);
        }

        return result;
    }

    public List<검색결과> 특정필드_검색후_다른필드_그룹결과(String 인덱스이름, String 특정필드, String 특정필드검색어, String 그룹할필드) throws IOException {
        SearchRequest searchRequest = new SearchRequest(인덱스이름);

        ExistsQueryBuilder existsQuery = QueryBuilders.existsQuery(특정필드);
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(existsQuery).filter(QueryBuilders.termQuery(특정필드, 특정필드검색어));
        searchRequest.source().query(boolQuery);

        TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("group_by_" + 특정필드)
                .field(그룹할필드)
                .size(1000); // Change the size as needed
        searchRequest.source().aggregation(termsAggregation);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        List<검색결과> groupedCounts = new ArrayList<>();
        Terms terms = searchResponse.getAggregations().get("group_by_" + 특정필드);
        for (Terms.Bucket bucket : terms.getBuckets()) {
            String bValue = bucket.getKeyAsString();
            long count = bucket.getDocCount();
            groupedCounts.add(new 검색결과(bValue, count));
        }

        return groupedCounts;
    }


    public <T> List<T>  getAllCreatedSince(final Date date,Class<T> valueType) {
        final SearchRequest request = this.buildSearchRequest(
                인덱스자료.지라이슈_인덱스명,
                "created",
                date
        );

        return searchInternal(request,valueType);
    }

    public <T> List<T>  searchCreatedSince(final 검색조건 dto, final Date date, Class<T> valueType) {
        final SearchRequest request = 검색엔진_유틸.buildSearchRequest(
                인덱스자료.지라이슈_인덱스명,
                dto,
                date
        );

        return searchInternal(request,valueType);
    }

    public Boolean index(final 지라이슈 지라_이슈) {
        try {
            final String vehicleAsString = MAPPER.writeValueAsString(지라_이슈);

            final IndexRequest request = new IndexRequest(인덱스자료.지라이슈_인덱스명);
            request.id(지라_이슈.getId());
            request.source(vehicleAsString, XContentType.JSON);

            final IndexResponse response = client.index(request, RequestOptions.DEFAULT);

            return response != null && response.status().equals(RestStatus.OK);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    public <T> T getById(final String 이슈_아이디,Class<T> valueType) {
        try {
            final GetResponse documentFields = client.get(
                    new GetRequest(인덱스자료.지라이슈_인덱스명, 이슈_아이디),
                    RequestOptions.DEFAULT
            );
            if (documentFields == null || documentFields.isSourceEmpty()) {
                return null;
            }

            return MAPPER.readValue(documentFields.getSourceAsString(), valueType);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final 검색조건 dto) {
        try {
            final int page = dto.getPage();
            final int size = dto.getSize();
            final int from = page <= 0 ? 0 : page * size;

            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .from(from)
                    .size(size)
                    .postFilter(getQueryBuilder(dto));

            if (dto.getSortBy() != null) {
                builder = builder.sort(
                        dto.getSortBy(),
                        dto.getOrder() != null ? dto.getOrder() : SortOrder.ASC
                );
            }

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final String field,
                                                   final Date date) {
        try {
            final SearchSourceBuilder builder = new SearchSourceBuilder()
                    .postFilter(getQueryBuilder(field, date));

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final 검색조건 dto,
                                                   final Date date) {
        try {
            final QueryBuilder searchQuery = getQueryBuilder(dto);
            final QueryBuilder dateQuery = getQueryBuilder("created", date);

            final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .mustNot(searchQuery)
                    .must(dateQuery);

            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .postFilter(boolQuery);

            if (dto.getSortBy() != null) {
                builder = builder.sort(
                        dto.getSortBy(),
                        dto.getOrder() != null ? dto.getOrder() : SortOrder.ASC
                );
            }

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final 검색조건 dto,
                                                   final Long 지라서버_아이디) {
        try {
            final int page = dto.getPage();
            final int size = dto.getSize();
            final int from = page <= 0 ? 0 : page * size;

            String 지라서버_아이디_패턴 = 지라서버_아이디 + "_*";
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.must(QueryBuilders.wildcardQuery("id", 지라서버_아이디_패턴));

            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .from(from)
                    .size(size)
                    .postFilter(getQueryBuilder(dto))
                    .query(boolQuery);

            if (dto.getSortBy() != null) {
                builder = builder.sort(
                        dto.getSortBy(),
                        dto.getOrder() != null ? dto.getOrder() : SortOrder.ASC
                );
            }

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static QueryBuilder getQueryBuilder(final 검색조건 dto) {
        if (dto == null) {
            return null;
        }

        final List<String> fields = dto.getFields();
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }

        if (fields.size() > 1) {
            final MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(dto.getSearchTerm())
                    .type(MultiMatchQueryBuilder.Type.CROSS_FIELDS)
                    .operator(Operator.AND);

            fields.forEach(queryBuilder::field);

            return queryBuilder;
        }

        return fields.stream()
                .findFirst()
                .map(field ->
                        QueryBuilders.matchQuery(field, dto.getSearchTerm())
                                .operator(Operator.AND))
                .orElse(null);
    }

    private static QueryBuilder getQueryBuilder(final String field, final Date date) {
        return QueryBuilders.rangeQuery(field).gte(date);
    }

    /* ***
     * 페이징 처리 데이터 조회 시 이슈 키 값으로 조회하면 같은 프로젝트명, 이슈 키 값이 있을 경우 문제가 있어 검색 조건 추가
     *** */
    public static SearchRequest buildSearchRequest(final String indexName,
                                                   final 검색조건 dto,
                                                   final Long serverId) {
        try {
            final int page = dto.getPage();
            final int size = dto.getSize();
            final int from = page <= 0 ? 0 : page * size;

            final QueryBuilder searchQuery = getQueryBuilder(dto);
            final QueryBuilder serverQuery = getQueryBuilder("jira_server_id", serverId);

            final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .must(searchQuery)
                    .must(serverQuery);

            SearchSourceBuilder builder = new SearchSourceBuilder()
                    .from(from)
                    .size(size)
                    .postFilter(boolQuery);

            if (dto.getSortBy() != null) {
                builder = builder.sort(
                        dto.getSortBy(),
                        dto.getOrder() != null ? dto.getOrder() : SortOrder.ASC
                );
            }

            final SearchRequest request = new SearchRequest(indexName);
            request.source(builder);

            return request;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static QueryBuilder getQueryBuilder(final String field, final Long serverId) {
        return QueryBuilders.matchQuery(field, serverId);
    }

}
