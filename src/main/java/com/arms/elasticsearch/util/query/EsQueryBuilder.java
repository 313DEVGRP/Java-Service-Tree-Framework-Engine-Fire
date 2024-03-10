package com.arms.elasticsearch.util.query;

import com.arms.elasticsearch.util.query.bool.EsBoolQuery;
import com.arms.elasticsearch.util.query.query_string.QueryString;
import com.arms.elasticsearch.util.query.sort.SortBy;
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


}
