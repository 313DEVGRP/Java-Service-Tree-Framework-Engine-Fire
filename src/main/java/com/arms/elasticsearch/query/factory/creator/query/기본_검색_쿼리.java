package com.arms.elasticsearch.query.factory.creator.query;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.기본_검색_요청;
import com.arms.elasticsearch.query.base.기본_검색_집계_요청;
import com.arms.elasticsearch.query.esquery.EsQueryBuilder;
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
public class 기본_검색_쿼리 implements 기본_쿼리 {

	private final int 크기;
	private final int 페이지;
	private final boolean 페이지_처리_여부;
	private final BoolQueryBuilder boolQuery;
	private final NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

	private 기본_검색_쿼리(기본_검색_요청 기본_검색_요청, EsQuery esQuery){
		this.크기 = 기본_검색_요청.get크기();
		this.페이지 = 기본_검색_요청.get페이지();
		this.페이지_처리_여부 = 기본_검색_요청.is페이지_처리_여부();
		this.boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});
		List<FieldSortBuilder> fieldSortBuilders = esQuery.getQuery(new ParameterizedTypeReference<>(){});
		EsQueryBuilder esQueryBuilder = new EsQueryBuilder().highlightAll();
		HighlightBuilder highlightBuilder = esQueryBuilder.getQuery(new ParameterizedTypeReference<>() {});

		nativeSearchQueryBuilder
			.withHighlightBuilder(highlightBuilder);

		nativeSearchQueryBuilder
			.withMaxResults(크기);

		if(페이지_처리_여부){
			nativeSearchQueryBuilder
				.withPageable(PageRequest.of(페이지,크기));
		}

		Optional.ofNullable(boolQuery)
			.ifPresent(nativeSearchQueryBuilder::withQuery);

		Optional.ofNullable(fieldSortBuilders)
			.ifPresent(sorts -> sorts.forEach(nativeSearchQueryBuilder::withSort));
	}

	public static 기본_검색_쿼리 of(기본_검색_요청 기본_검색_요청, EsQuery esQuery){
		return new 기본_검색_쿼리(기본_검색_요청, esQuery);
	}

	public static 기본_검색_쿼리 of(EsQuery esQuery){
		return new 기본_검색_쿼리(new 기본_검색_집계_요청() {},esQuery);
	}

	@Override
	public NativeSearchQuery 생성() {
		return nativeSearchQueryBuilder.build();
	}
}
