package com.arms.elasticsearch.util.query;

import com.arms.elasticsearch.util.query.bool.EsBoolQuery;
import com.arms.elasticsearch.util.query.sort.SortBy;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EsQueryBuilder extends EsQuery {

	public EsQueryBuilder bool(EsBoolQuery ... esBoolQuery){

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		for (EsBoolQuery esQuery : esBoolQuery) {
			esQuery.boolQueryBuilder(boolQueryBuilder);
		}
		put(new ParameterizedTypeReference<>(){},boolQueryBuilder);

		return this;
	}

	public EsQueryBuilder sort(SortBy sortBy){
		put(new ParameterizedTypeReference<>(){},sortBy.sortQuery());
		return this;
	}

}
