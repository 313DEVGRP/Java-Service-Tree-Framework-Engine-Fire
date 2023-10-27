package com.arms.elasticsearch.util.query;

import java.util.List;
import java.util.Optional;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.arms.elasticsearch.util.base.검색_기본_요청;
import com.arms.elasticsearch.util.query.bool.조건_쿼리_컴포넌트;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class 검색_일반_요청 implements 쿼리_추상_팩토리 {

	private final List<String> 하위그룹필드들;
	private final String 메인그룹필드;
	private final int 크기;
	private final boolean 컨텐츠보기여부;
	private final 조건_쿼리_컴포넌트 조건_쿼리_컴포넌트;

	private 검색_일반_요청(검색_기본_요청 검색_기본_요청, 조건_쿼리_컴포넌트 조건_쿼리_컴포넌트){
		this.하위그룹필드들 = 검색_기본_요청.get하위그룹필드들();
		this.메인그룹필드 = 검색_기본_요청.get메인그룹필드();
		this.크기 = 검색_기본_요청.get크기();
		this.컨텐츠보기여부 = 검색_기본_요청.is컨텐츠보기여부();
		this.조건_쿼리_컴포넌트 = 조건_쿼리_컴포넌트;
	}

	public static 쿼리_추상_팩토리 of(검색_기본_요청 검색_기본_요청, 조건_쿼리_컴포넌트 조건_쿼리_컴포넌트){
		return new 검색_일반_요청(검색_기본_요청, 조건_쿼리_컴포넌트);
	}

	@Override
	public NativeSearchQuery 생성() {

		BoolQueryBuilder boolQuery = 조건_쿼리_컴포넌트.getBoolQuery();
		서브_집계_요청 서브_집계_요청 = new 서브_집계_요청(하위그룹필드들, 크기);

		NativeSearchQueryBuilder nativeSearchQueryBuilder
			= new NativeSearchQueryBuilder()
			.withMaxResults(컨텐츠보기여부 ? 크기 : 0);

		Optional.of(boolQuery)
			.ifPresent(query->{
				nativeSearchQueryBuilder.withQuery(boolQuery);
			});

		Optional.ofNullable(메인그룹필드)
			.ifPresent(그룹_필드 -> {
				TermsAggregationBuilder termsAggregationBuilder
					= AggregationBuilders.terms("group_by_" + 그룹_필드)
						.field(그룹_필드)
						.size(크기);
				nativeSearchQueryBuilder.withAggregations(
					termsAggregationBuilder
				);
				Optional.ofNullable(하위그룹필드들)
					.ifPresent(하위그룹필드들->{
						if(!하위그룹필드들.isEmpty()){
							termsAggregationBuilder
								.subAggregation(
										서브_집계_요청.createNestedAggregation(하위그룹필드들, 크기)
								);
						}
					});
			});

		return nativeSearchQueryBuilder.build();
	}
}
