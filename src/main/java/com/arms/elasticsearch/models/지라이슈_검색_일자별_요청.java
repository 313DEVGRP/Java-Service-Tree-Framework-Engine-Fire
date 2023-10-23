package com.arms.elasticsearch.models;

import com.arms.elasticsearch.util.쿼리_추상_팩토리;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;


@Setter
@Getter
public class 지라이슈_검색_일자별_요청 implements 쿼리_추상_팩토리 {

	private String 서비스아이디;
	private String 특정필드;
	private String 특정필드검색어;
	private List<String> 그룹필드들;
	private String 시간그룹필드;
	private int size = 1000;
	private boolean historyView = false;
	private boolean issueRequest;
	private String 필터필드;
	private List<?> 필터필드검색어;

	@Override
	public NativeSearchQuery 생성() {

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		searchService(boolQuery);
		isReqQuery(boolQuery);
		searchField(boolQuery);
		searchFilter(boolQuery);

		return new NativeSearchQueryBuilder()
		    .withQuery(boolQuery)
			.withMaxResults(historyView?size:0)
			.withAggregations(
				new DateHistogramAggregationBuilder("date_group_by_"+시간그룹필드)
					.field(시간그룹필드)  // 날짜 필드 이름을 지정
					.calendarInterval(DateHistogramInterval.DAY)  // 집계 간격을 지정
					.subAggregation(
						this.createNestedAggregation(그룹필드들,size)
					)
					.minDocCount(0)
			)
		    .build();
	}


	public AggregationBuilder createNestedAggregation(List<String> 그룹필드들, int size) {
		return
			 그룹필드들
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

	public BoolQueryBuilder searchFilter(BoolQueryBuilder boolQuery){
		if(!ObjectUtils.isEmpty(필터필드)){
			boolQuery.filter(QueryBuilders.termsQuery(필터필드, 필터필드검색어));
		}
		return boolQuery;
	}


}
