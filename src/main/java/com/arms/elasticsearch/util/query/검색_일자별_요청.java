package com.arms.elasticsearch.util.query;

import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Optional;

import com.arms.elasticsearch.util.base.검색_기본_요청;
import com.arms.elasticsearch.util.query.bool.EsQuery;

@Setter
@Getter
public class 검색_일자별_요청 implements 쿼리_추상_팩토리 {

	private final List<String> 하위그룹필드들;
	private final String 메인그룹필드;
	private final int 크기;
	private final boolean 컨텐츠보기여부;

	private final EsQuery esQuery;

	private 검색_일자별_요청(검색_기본_요청 검색_기본_요청, EsQuery esQuery){
		this.하위그룹필드들 = 검색_기본_요청.get하위그룹필드들();
		this.메인그룹필드 = 검색_기본_요청.get메인그룹필드();
		this.크기 = 검색_기본_요청.get크기();
		this.컨텐츠보기여부 = 검색_기본_요청.is컨텐츠보기여부();
		this.esQuery = esQuery;
	}

	public static 쿼리_추상_팩토리 of(검색_기본_요청 검색_기본_요청, EsQuery esQuery){
		return new 검색_일자별_요청(검색_기본_요청, esQuery);
	}

	@Override
	public NativeSearchQuery 생성() {
		BoolQueryBuilder boolQuery = esQuery.boolQuery();
		서브_집계_요청 서브_집계_요청 = new 서브_집계_요청(하위그룹필드들, 크기);

		NativeSearchQueryBuilder nativeSearchQueryBuilder
			= new NativeSearchQueryBuilder()
			.withMaxResults(컨텐츠보기여부 ? 크기 : 0);

		Optional.of(boolQuery)
			.ifPresent(query->{
				nativeSearchQueryBuilder.withQuery(boolQuery);
			});

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
