package com.arms.elasticsearch.models;

import com.arms.elasticsearch.util.query.서브_집계_요청;
import com.arms.elasticsearch.util.query.쿼리_추상_팩토리;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;


@Getter
@Setter
public class 지라이슈_검색_일반_요청_요구사항여부제외 implements 쿼리_추상_팩토리 {

	private Long 서비스아이디;
	private String 특정필드;
	private String 특정필드검색어;

	private List<String> 하위그룹필드들;
	private String 메인그룹필드;
	private int 크기 = 1000;
	private boolean 컨텐츠보기여부;

	@Override
	public NativeSearchQuery 생성() {

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		searchService(boolQuery);
		서브_집계_요청 서브_집계_요청 = new 서브_집계_요청(하위그룹필드들, 크기);

		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
				.withMaxResults(컨텐츠보기여부 ? 크기 : 0);

		Optional.of(boolQuery)
				.ifPresent(query->{
					nativeSearchQueryBuilder
							.withQuery(boolQuery);
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



	public BoolQueryBuilder searchService(BoolQueryBuilder boolQuery){

		if(!ObjectUtils.isEmpty(서비스아이디)){
			boolQuery.must(QueryBuilders.termQuery("pdServiceId", 서비스아이디));
		}
		return boolQuery;
	}


}
