package com.arms.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
public final class 검색엔진_유틸 {

    public static NativeSearchQueryBuilder buildSearchQuery(final 검색조건 dto) {
        try {
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(getQueryBuilder(dto))
                .withPageable(PageRequest.of(dto.getPage(), dto.getSize()));
                if(dto.getSortBy()!=null){
                    nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(dto.getSortBy())
                        .order(Optional.ofNullable(dto.getOrder()).orElseGet(() -> SortOrder.ASC)));
                }

                return nativeSearchQueryBuilder;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* ***
     * 페이징 처리 데이터 조회 시 이슈 키 값으로 조회하면 같은 프로젝트명, 이슈 키 값이 있을 경우 문제가 있어 검색 조건 추가
     *** */
    public static NativeSearchQueryBuilder buildSearchQuery( final 검색조건 dto ,final Long serverId) {
        try {
            final QueryBuilder searchQuery = getQueryBuilder(dto);
            final QueryBuilder serverQuery = getQueryBuilder("jira_server_id", serverId);

            final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(searchQuery)
                .must(serverQuery);

            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(dto.getPage(), dto.getSize()));

            if(dto.getSortBy()!=null){
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(dto.getSortBy())
                    .order(Optional.ofNullable(dto.getOrder()).orElseGet(() -> SortOrder.ASC)));
            }

            return nativeSearchQueryBuilder;
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static NativeSearchQueryBuilder buildSearchQuery(final String field,final Date date) {
        try {

            return new NativeSearchQueryBuilder()
                .withQuery(getQueryBuilder(field, date));

        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static NativeSearchQueryBuilder buildSearchQuery(final 검색조건 dto, final Date date) {
        try {
            final QueryBuilder searchQuery = getQueryBuilder(dto);
            final QueryBuilder dateQuery = getQueryBuilder("created", date);

            final BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                    .mustNot(searchQuery)
                    .must(dateQuery);

            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withPageable(PageRequest.of(dto.getPage(), dto.getSize()));
            if(dto.getSortBy()!=null){
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(dto.getSortBy())
                    .order(Optional.ofNullable(dto.getOrder()).orElseGet(() -> SortOrder.ASC)));
            }
            return nativeSearchQueryBuilder;
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


    private static QueryBuilder getQueryBuilder(final String field, final Long serverId) {
        return QueryBuilders.matchQuery(field, serverId);
    }

}
