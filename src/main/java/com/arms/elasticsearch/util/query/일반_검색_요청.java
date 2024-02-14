package com.arms.elasticsearch.util.query;

import com.arms.elasticsearch.util.base.기본_검색_요청;
import com.arms.elasticsearch.util.base.기본_집계_요청;
import lombok.Getter;
import lombok.Setter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Optional;

@Setter
@Getter
public class 일반_검색_요청 implements 쿼리_추상_팩토리 {

	private final int 크기;
	private final EsQuery esQuery;

	private 일반_검색_요청(기본_검색_요청 기본_검색_요청, EsQuery esQuery){
		this.크기 = 기본_검색_요청.get크기();
		this.esQuery = esQuery;
	}

	public static 쿼리_추상_팩토리 of(기본_검색_요청 기본_검색_요청, EsQuery esQuery){
		return new 일반_검색_요청(기본_검색_요청, esQuery);
	}

	@Override
	public NativeSearchQuery 생성() {
		BoolQueryBuilder boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});
		FieldSortBuilder sort = esQuery.getQuery(new ParameterizedTypeReference<>(){});

		NativeSearchQueryBuilder nativeSearchQueryBuilder
			= new NativeSearchQueryBuilder()
			.withMaxResults(크기);

		Optional.of(boolQuery)
			.ifPresent(query->{
				nativeSearchQueryBuilder.withQuery(boolQuery);
			});

		if(sort!=null){
			nativeSearchQueryBuilder.withSort(sort);
		}

		return nativeSearchQueryBuilder.build();
	}
}
