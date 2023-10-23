package com.arms.elasticsearch.models;

import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.ObjectUtils;

import com.arms.elasticsearch.util.쿼리_추상_팩토리;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class 지라이슈_검색_일반_요청 implements 쿼리_추상_팩토리 {

	private Long 서비스아이디;
	private String 특정필드;
	private String 특정필드검색어;

	private List<String> 하위그룹필드들;

	private String 메인그룹필드;
	private int size = 1000;
	private boolean historyView = false;
	private boolean issueRequest;

	@Override
	public NativeSearchQuery 생성() {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		isReqQuery(boolQuery);
		searchService(boolQuery);

		return new NativeSearchQueryBuilder()
		    .withQuery(boolQuery)
			.withMaxResults(historyView?size:0)
		    .withAggregations(
		        AggregationBuilders.terms( "group_by_"+메인그룹필드)
		            .field(메인그룹필드)
					.subAggregation(
						this.createNestedAggregation(하위그룹필드들,size)
					)
		            .size(size)
		    )
		    .build();
	}

	public AggregationBuilder createNestedAggregation(List<String> 하위_그룹필드들, int size) {
		return
			하위_그룹필드들
				.stream()
				.map(groupField ->
					AggregationBuilders.terms("group_by_" + groupField)
						.field(groupField)
						.size(size))
				.reduce(null, (agg1, agg2) -> {
					if (agg1 == null) {
						return agg2;
					} else {
						return agg1.subAggregation(agg2);
					}
				});
	}


	public BoolQueryBuilder searchService(BoolQueryBuilder boolQuery){

		if(!ObjectUtils.isEmpty(서비스아이디)){
			boolQuery.must(QueryBuilders.termQuery("pdServiceId", 서비스아이디));
		}
		return boolQuery;
	}

	public BoolQueryBuilder searchField(BoolQueryBuilder boolQuery){

		if(!ObjectUtils.isEmpty(특정필드)){
			boolQuery.must(QueryBuilders.termQuery(특정필드, 특정필드검색어));
		}
		return boolQuery;
	}

	public BoolQueryBuilder isReqQuery(BoolQueryBuilder boolQuery){
		boolQuery.must(QueryBuilders.termQuery("isReq", issueRequest));
		return boolQuery;
	}
}
