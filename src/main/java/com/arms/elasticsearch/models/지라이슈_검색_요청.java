package com.arms.elasticsearch.models;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.arms.elasticsearch.repositories.QueryAbstractFactory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class 지라이슈_검색_요청 implements QueryAbstractFactory {


	// NativeSearchQuery query = new NativeSearchQueryBuilder()
	//     .withQuery(QueryBuilders.termQuery("isReq", true))
	//     .withPageable(PageRequest.of(0, 1000))
	//     .withAggregations(
	//         AggregationBuilders.terms( "groupId")
	//             .field("pdServiceVersion")
	//             .size(1000)
	//     )
	//     .build();

	private String 특정필드;
	private String 특정필드검색어;

	private String 그룹할필드;
	private int page = 0;
	private int size = 1000;

	private boolean historyView = false;

	@Override
	public NativeSearchQuery create() {

		return new NativeSearchQueryBuilder()
		    .withQuery(QueryBuilders.termQuery(특정필드, 특정필드검색어))
			.withMaxResults(historyView?size:0)
		    .withAggregations(
		        AggregationBuilders.terms( "group_by_"+그룹할필드)
		            .field(그룹할필드)
		            .size(size)
		    )
		    .build();
	}
}
