package com.arms.elasticsearch.query.factory;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.평균_및_최대치_집계_요청;
import com.arms.elasticsearch.query.쿼리_추상_팩토리;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValuesSourceAggregationBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Setter
@Getter
	public class 평균_및_최대치_집계_쿼리_생성기 implements 쿼리_추상_팩토리 {

	private final List<String> 그룹_필드들;
	private final String 메인_그룹_필드;
	private final EsQuery esQuery;
	private final NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
	private final TermsAggregationBuilder termsAggregationBuilder;

	private 평균_및_최대치_집계_쿼리_생성기(평균_및_최대치_집계_요청 평균_및_최대치_집계_요청, EsQuery esQuery){
		this.termsAggregationBuilder
			= AggregationBuilders.terms("group_by_" + 평균_및_최대치_집계_요청.get메인_그룹_필드())
				.field(평균_및_최대치_집계_요청.get메인_그룹_필드());

		nativeSearchQueryBuilder.addAggregation(
				this.termsAggregationBuilder
		);

		Optional.of(평균_및_최대치_집계_요청.get하위크기()).filter(a->a>0)
			.ifPresent(a->{
				this.termsAggregationBuilder.size(평균_및_최대치_집계_요청.get하위크기());
			});

		this.메인_그룹_필드 = 평균_및_최대치_집계_요청.get메인_그룹_필드();
		this.그룹_필드들 = 평균_및_최대치_집계_요청.get하위_그룹_필드들();
		this.esQuery = esQuery;
	}

	public static 쿼리_추상_팩토리 of(평균_및_최대치_집계_요청 평균_및_최대치_집계_요청, EsQuery esQuery){
		return new 평균_및_최대치_집계_쿼리_생성기(평균_및_최대치_집계_요청, esQuery);
	}

	@Override
	public NativeSearchQuery 생성() {
		BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});

		Optional.ofNullable(boolQuery)
				.ifPresent(query-> nativeSearchQueryBuilder.withQuery(boolQuery));

		aggregation((그룹_필드) -> AggregationBuilders.avg("avg_by_" + 그룹_필드));
		aggregation((그룹_필드) -> AggregationBuilders.max("max_by_" + 그룹_필드));

		return nativeSearchQueryBuilder.build();
	}

	private <T extends ValuesSourceAggregationBuilder<T>> void aggregation(Function<String,ValuesSourceAggregationBuilder<T>> aggregationBuilders) {

		그룹_필드들.forEach(그룹_필드-> termsAggregationBuilder
			.subAggregation(
				aggregationBuilders.apply(그룹_필드)
					.field(그룹_필드)
			)
		);
	}
}
