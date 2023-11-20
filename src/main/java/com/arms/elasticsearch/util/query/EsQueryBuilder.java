package com.arms.elasticsearch.util.query;

import com.arms.elasticsearch.util.query.bool.EsBoolQuery;
import com.arms.elasticsearch.util.query.sort.SortBy;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EsQueryBuilder implements EsQuery {

	private List<EsBoolQuery> esQueryList;
	private final Map<ParameterizedTypeReference<?>,Object> map = new ConcurrentHashMap<>();

	public void boolQuery(){
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		for (EsBoolQuery esQuery : esQueryList) {
			esQuery.boolQueryBuilder(boolQueryBuilder);
		}
	}

	public EsQueryBuilder bool(EsBoolQuery ... esBoolQuery){
		this.esQueryList = Arrays.asList(esBoolQuery);
		map.put(new ParameterizedTypeReference<BoolQueryBuilder>(){},this.esQueryList);
		return this;
	}

	public EsQueryBuilder sort(SortBy sortBy){
		map.put(new ParameterizedTypeReference<SortBy>(){},sortBy.sortQuery());
		return this;
	}

	@Override
	public <T> T getQuery(ParameterizedTypeReference<T> typeReference) {
		return ((Class<T>)typeReference.getClass()).cast(map.get(typeReference));
	}


}
