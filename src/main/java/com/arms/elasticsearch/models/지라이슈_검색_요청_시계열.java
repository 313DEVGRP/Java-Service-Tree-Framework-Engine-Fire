package com.arms.elasticsearch.models;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.arms.elasticsearch.repositories.QueryAbstractFactory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class 지라이슈_검색_요청_시계열 implements QueryAbstractFactory {

	private String 특정필드;
	private String 특정필드검색어;
	private String 그룹할필드;
	private int size = 1000;


	private boolean historyView = false;

	@Override
	public NativeSearchQuery create() {

		return new NativeSearchQueryBuilder()
		    .withQuery(QueryBuilders.termQuery(특정필드, 특정필드검색어))
			.withMaxResults(historyView?size:0)
			.withAggregations(
				new DateHistogramAggregationBuilder("date_group_by_"+그룹할필드)
					.field(그룹할필드)  // 날짜 필드 이름을 지정
					.calendarInterval(DateHistogramInterval.DAY)  // 집계 간격을 지정
					.minDocCount(0)
			)
		    .build();
	}
}
