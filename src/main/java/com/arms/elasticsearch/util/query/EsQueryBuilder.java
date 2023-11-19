package com.arms.elasticsearch.util.query;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Sort;

import com.arms.elasticsearch.util.query.bool.EsBoolQuery;
import com.arms.elasticsearch.util.query.sort.SortBy;

public class EsQueryBuilder implements EsQuery {

	private BoolQueryBuilder boolQueryBuilder;
	private Sort sort ;
	private List<EsBoolQuery> esQueryList = new ArrayList<>();

	public EsQueryBuilder(){
		this.boolQueryBuilder = QueryBuilders.boolQuery();
	}

	@Override
	public BoolQueryBuilder boolQuery(){
		for (EsBoolQuery esQuery : esQueryList) {
			esQuery.boolQueryBuilder(boolQueryBuilder);
		}
		return this.boolQueryBuilder;
	}

	public EsQueryBuilder bool(EsBoolQuery esBoolQuery){
		this.esQueryList.add(esBoolQuery);
		return this;
	}

	@Override
	public Sort sortQuery() {
		return this.sort;
	};

	public EsQueryBuilder sort(SortBy sortBy){
		this.sort = sortBy.sortQuery();
		return this;
	}


}
