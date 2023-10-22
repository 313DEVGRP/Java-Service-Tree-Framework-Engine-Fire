package com.arms.elasticsearch.models;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.ObjectUtils;

import com.arms.elasticsearch.util.쿼리_추상_팩토리;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class 지라이슈_검색_요청 implements 쿼리_추상_팩토리 {

	private String 서비스아이디;
	private String 특정필드;
	private String 특정필드검색어;
	private String 그룹할필드;

	private int size = 1000;
	private boolean historyView = false;

	@Override
	public NativeSearchQuery 생성() {

		BoolQueryBuilder boolQuery
			= boolQuery().must(QueryBuilders.termQuery("issuetype.issuetype_subtask", true));
		boolQuery.must(QueryBuilders.termQuery("pdServiceId", 서비스아이디));

		return new NativeSearchQueryBuilder()
		    .withQuery(boolQuery)
			.withMaxResults(historyView?size:0)
		    .withAggregations(
		        AggregationBuilders.terms( "group_by_"+그룹할필드)
		            .field(그룹할필드)
		            .size(size)
		    )
		    .build();
	}

	public BoolQueryBuilder boolQuery(){
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if(!ObjectUtils.isEmpty(특정필드)){
			boolQuery.must(QueryBuilders.termQuery(특정필드, 특정필드검색어));
		}
		return boolQuery;
	}
}
