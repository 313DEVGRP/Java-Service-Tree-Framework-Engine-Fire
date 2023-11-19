package com.arms.elasticsearch.util.query.bool;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class BoolQuery implements EsQuery {

	private final BoolQueryBuilder boolQueryBuilder;

	private List<EsQuery> esQueryList = new ArrayList<>();

	public BoolQuery(){
		this.boolQueryBuilder = QueryBuilders.boolQuery();
	}

	@Override
	public BoolQueryBuilder boolQuery(){
		for (EsQuery esQuery : esQueryList) {
			esQuery.boolQueryBuilder(boolQueryBuilder);
		}
		return boolQueryBuilder;
	}

	public BoolQuery add(EsQuery esQuery){
		esQueryList.add(esQuery);
		return this;
	}


}
