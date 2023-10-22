package com.arms.elasticsearch.models;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.ObjectUtils;

import com.arms.elasticsearch.util.쿼리_추상_팩토리;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class 지라이슈_검색_요청_시계열 implements 쿼리_추상_팩토리 {

	private String 서비스아이디;
	private String 특정필드;
	private String 특정필드검색어;
	private String 그룹할필드;

	private String 하위_그룹할필드;
	private int size = 1000;
	private boolean historyView = false;

	private boolean isReq;

	@Override
	public NativeSearchQuery 생성() {

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		searchService(boolQuery);
		isReqQuery(boolQuery);
		searchField(boolQuery);

		return new NativeSearchQueryBuilder()
		    .withQuery(boolQuery)
			.withMaxResults(historyView?size:0)
			.withAggregations(
				AggregationBuilders.terms( "group_by_"+그룹할필드)
					.field(그룹할필드)
					.size(size)
					.subAggregation(
						new DateHistogramAggregationBuilder("date_group_by_"+하위_그룹할필드)
							.field(하위_그룹할필드)  // 날짜 필드 이름을 지정
							.calendarInterval(DateHistogramInterval.DAY)  // 집계 간격을 지정
							.minDocCount(0)
					)
			)
		    .build();
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
		boolQuery.must(QueryBuilders.termQuery("isReq", isReq));
		return boolQuery;
	}


}
