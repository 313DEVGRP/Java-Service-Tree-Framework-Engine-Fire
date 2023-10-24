package com.arms.elasticsearch.models;

import java.util.List;
import java.util.Optional;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
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
	private int 크기 = 1000;
	private boolean 컨텐츠보기여부 = false;
	private boolean 요구사항인지여부;

	@Override
	public NativeSearchQuery 생성() {

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		isReqQuery(boolQuery);
		searchService(boolQuery);

		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder()
			.withQuery(boolQuery)
			.withMaxResults(컨텐츠보기여부 ? 크기 : 0);

		Optional.ofNullable(메인그룹필드)
			.ifPresent(그룹_필드 -> {
				TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("group_by_" + 그룹_필드)
					.field(그룹_필드)
					.size(크기);
				nativeSearchQueryBuilder.withAggregations(
					termsAggregationBuilder
				);
				Optional.ofNullable(하위그룹필드들)
					.ifPresent(하위그룹필드들->{
						if(!하위그룹필드들.isEmpty()){
							termsAggregationBuilder.subAggregation(this.createNestedAggregation(하위그룹필드들, 크기));
						}
					});
			});

		return nativeSearchQueryBuilder.build();

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
		boolQuery.must(QueryBuilders.termQuery("isReq", 요구사항인지여부));
		return boolQuery;
	}
}
