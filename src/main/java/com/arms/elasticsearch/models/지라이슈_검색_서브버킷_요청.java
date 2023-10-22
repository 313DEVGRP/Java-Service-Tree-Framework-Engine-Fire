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


@Getter
@Setter
public class 지라이슈_검색_서브버킷_요청 implements 쿼리_추상_팩토리 {

	private String 서비스아이디;
	private String 특정필드;
	private String 특정필드검색어;
	private String 그룹할필드;
	private String 하위_그룹할필드;
	private int size;
	private boolean historyView;

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
					.subAggregation(AggregationBuilders.terms( "group_by_"+하위_그룹할필드)
						.field(하위_그룹할필드)
						.size(size))
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
