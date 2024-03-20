package com.arms.elasticsearch.query;

import com.arms.elasticsearch.query.base.기본_검색_요청;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Optional;

@Setter
@Getter
public class 일반_검색_요청 implements 쿼리_추상_팩토리 {

	private final int 크기;
	private final int 페이지;
	private final EsQuery esQuery;

	private 일반_검색_요청(기본_검색_요청 기본_검색_요청, EsQuery esQuery){
		this.크기 = 기본_검색_요청.get크기();
		this.페이지 = 기본_검색_요청.get페이지();
		this.esQuery = esQuery;
	}

	public static 쿼리_추상_팩토리 of(기본_검색_요청 기본_검색_요청, EsQuery esQuery){
		return new 일반_검색_요청(기본_검색_요청, esQuery);
	}

	@Override
	public NativeSearchQuery 생성() {
		BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});
		List<FieldSortBuilder> fieldSortBuilders = esQuery.getQuery(new ParameterizedTypeReference<>(){});

		NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

		HighlightBuilder highlightBuilder = new HighlightBuilder();
		highlightBuilder.field("*").preTags("<em>").postTags("</em>");
		nativeSearchQueryBuilder.withHighlightBuilder(highlightBuilder);

		Optional.ofNullable(크기)
				.ifPresent(크기->{
					nativeSearchQueryBuilder
							.withMaxResults(크기);
					Optional.ofNullable(페이지)
							.ifPresent(페이지->{
								nativeSearchQueryBuilder
										.withPageable(PageRequest.of(페이지,크기));
							});
				});

		Optional.ofNullable(boolQuery)
				.ifPresent(nativeSearchQueryBuilder::withQuery);


		Optional.ofNullable(fieldSortBuilders)
				.ifPresent(sorts -> {
					sorts.forEach(nativeSearchQueryBuilder::withSort);
				});

		return nativeSearchQueryBuilder.build();
	}
}
