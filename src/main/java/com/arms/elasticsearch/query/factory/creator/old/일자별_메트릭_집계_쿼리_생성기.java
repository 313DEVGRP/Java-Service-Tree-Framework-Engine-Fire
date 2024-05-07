package com.arms.elasticsearch.query.factory.creator.old;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.메트릭_집계_요청;
import com.arms.elasticsearch.query.쿼리_생성기;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregationBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Setter
@Getter
	public class 일자별_메트릭_집계_쿼리_생성기 implements 쿼리_생성기 {

	private final List<String> 그룹_필드들;
	private final String 메인_그룹_필드;
	private final int 크기;
	private final boolean 컨텐츠보기여부;
	private final EsQuery esQuery;
	private final NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
	private final DateHistogramAggregationBuilder dateHistogramAggregationBuilder;

	private 일자별_메트릭_집계_쿼리_생성기(메트릭_집계_요청 메트릭_집계_요청, EsQuery esQuery){
		this.컨텐츠보기여부 = 메트릭_집계_요청.is컨텐츠보기여부();
		this.dateHistogramAggregationBuilder = new DateHistogramAggregationBuilder(
			"date_group_by_" + 메트릭_집계_요청.get메인_그룹_필드())
			.field(메트릭_집계_요청.get메인_그룹_필드())
			.calendarInterval(DateHistogramInterval.DAY)
			.minDocCount(0); // 집계 간격을 지정

		nativeSearchQueryBuilder.addAggregation(
			dateHistogramAggregationBuilder
		);

		this.메인_그룹_필드 = 메트릭_집계_요청.get메인_그룹_필드();
		this.그룹_필드들 = 메트릭_집계_요청.get하위_그룹_필드들();
		this.esQuery = esQuery;
		this.크기 = 메트릭_집계_요청.get크기();
	}

	public static 쿼리_생성기 of(메트릭_집계_요청 메트릭_집계_요청, EsQuery esQuery){
		return new 일자별_메트릭_집계_쿼리_생성기(메트릭_집계_요청, esQuery);
	}

	@Override
	public NativeSearchQuery 생성() {
		BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

		Optional.ofNullable(boolQuery)
			.ifPresent(query-> {
				nativeSearchQueryBuilder
						.withQuery(boolQuery)
					.withMaxResults(컨텐츠보기여부 ? 크기 : 0);
			});

		subAggregation((그룹_필드) -> AggregationBuilders.avg("avg_by_" + 그룹_필드));
		subAggregation((그룹_필드) -> AggregationBuilders.max("max_by_" + 그룹_필드));
		subAggregation((그룹_필드) -> AggregationBuilders.min("min_by_" + 그룹_필드));

		return nativeSearchQueryBuilder.build();
	}

	private <T extends ValuesSourceAggregationBuilder<T>> void subAggregation(Function<String,ValuesSourceAggregationBuilder<T>> aggregationBuilders) {

		그룹_필드들.forEach(그룹_필드-> this.dateHistogramAggregationBuilder
			.subAggregation(
				aggregationBuilders.apply(그룹_필드)
					.field(그룹_필드)
			)
		);
	}
}
