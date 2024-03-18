package com.arms.elasticsearch.query;

import com.arms.elasticsearch.query.bool.EsBoolQuery;
import com.arms.elasticsearch.query.bool.RangeQueryFilter;
import com.arms.elasticsearch.query.query_string.QueryString;
import com.arms.elasticsearch.query.sort.SortBy;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.core.ParameterizedTypeReference;

public class EsQueryBuilder extends EsQuery {

	public EsQueryBuilder bool(EsBoolQuery ... esBoolQuery){
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		for (EsBoolQuery esQuery : esBoolQuery) {
			esQuery.boolQueryBuilder(boolQueryBuilder);
		}
		setQuery(new ParameterizedTypeReference<>() {},boolQueryBuilder);
		return this;
	}

	public EsQueryBuilder sort(SortBy sortBy){
		setQuery(new ParameterizedTypeReference<>() {},sortBy.sortBy());
		return this;
	}

	public EsQueryBuilder queryString(QueryString queryString){
		setQuery(new ParameterizedTypeReference<>() {},queryString.queryString());
		return this;
	}

	public EsQueryBuilder rangeQueryBuilder(RangeQueryFilter rangeQueryFilter) {
		setQuery(new ParameterizedTypeReference<>() {},rangeQueryFilter);
		return this;
	}
}
