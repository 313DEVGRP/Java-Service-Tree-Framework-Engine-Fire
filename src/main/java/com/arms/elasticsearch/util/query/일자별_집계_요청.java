package com.arms.elasticsearch.util.query;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Optional;

import com.arms.elasticsearch.util.base.기본_집계_요청;

@Setter
@Getter
public class 일자별_집계_요청 implements 쿼리_추상_팩토리 {

	private final List<String> 하위그룹필드들;
	private final String 메인그룹필드;
	private final int 크기;
	private final boolean 컨텐츠보기여부;

	private final EsQuery esQuery;

	private 일자별_집계_요청(기본_집계_요청 기본_집계_요청, EsQuery esQuery){
		this.하위그룹필드들 = 기본_집계_요청.get하위그룹필드들();
		this.메인그룹필드 = 기본_집계_요청.get메인그룹필드();
		this.크기 = 기본_집계_요청.get크기();
		this.컨텐츠보기여부 = 기본_집계_요청.is컨텐츠보기여부();
		this.esQuery = esQuery;
	}

	public static 쿼리_추상_팩토리 of(기본_집계_요청 기본_집계_요청, EsQuery esQuery){
		return new 일자별_집계_요청(기본_집계_요청, esQuery);
	}

	@Override
	public NativeSearchQuery 생성() {
		BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>(){});

		Sort sort = esQuery.getQuery(new ParameterizedTypeReference<>(){});

		서브_집계_요청 서브_집계_요청 = new 서브_집계_요청(하위그룹필드들, 크기);

		NativeSearchQueryBuilder nativeSearchQueryBuilder
			= new NativeSearchQueryBuilder()
			.withMaxResults(컨텐츠보기여부 ? 크기 : 0);

		Optional.of(boolQuery)
			.ifPresent(query->{
				nativeSearchQueryBuilder.withQuery(boolQuery);
			});

		if(sort!=null){
			nativeSearchQueryBuilder.withSort(sort);
		}

		Optional.ofNullable(메인그룹필드)
			.ifPresent(시간그룹필드 -> {
				DateHistogramAggregationBuilder dateHistogramAggregationBuilder = new DateHistogramAggregationBuilder(
					"date_group_by_" + 시간그룹필드)
					.field(시간그룹필드)
					.calendarInterval(DateHistogramInterval.DAY)
					.minDocCount(0); // 집계 간격을 지정
				nativeSearchQueryBuilder.withAggregations(
					dateHistogramAggregationBuilder
				);
				Optional.ofNullable(하위그룹필드들)
					.ifPresent(하위그룹필드들->{
						if(!하위그룹필드들.isEmpty()){
							dateHistogramAggregationBuilder.subAggregation(서브_집계_요청.createNestedAggregation(하위그룹필드들, 크기));
						}
					});
			});

		return nativeSearchQueryBuilder.build();

	}


}
