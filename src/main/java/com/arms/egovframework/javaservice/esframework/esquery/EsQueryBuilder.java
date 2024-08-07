package com.arms.egovframework.javaservice.esframework.esquery;

import com.arms.egovframework.javaservice.esframework.EsQuery;
import com.arms.egovframework.javaservice.esframework.filter.RangeQueryFilter;
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

	public EsQueryBuilder sort(EsSortQuery esSortQuery){
		setQuery(new ParameterizedTypeReference<>() {}, esSortQuery.sortBy());
		return this;
	}

	public EsQueryBuilder queryString(EsQueryString esQueryString){
		setQuery(new ParameterizedTypeReference<>() {}, esQueryString.queryString());
		return this;
	}

	public EsQueryBuilder highlight(EsHighlight esHighlight){
		setQuery(new ParameterizedTypeReference<>() {}, esHighlight.getHighlight());
		return this;
	}

	public EsQueryBuilder highlightAll(){
		setQuery(new ParameterizedTypeReference<>() {}, new EsHighlight().getHighlight());
		return this;
	}

	public EsQueryBuilder rangeQueryBuilder(RangeQueryFilter rangeQueryFilter) {
		setQuery(new ParameterizedTypeReference<>() {},rangeQueryFilter);
		return this;
	}
}
