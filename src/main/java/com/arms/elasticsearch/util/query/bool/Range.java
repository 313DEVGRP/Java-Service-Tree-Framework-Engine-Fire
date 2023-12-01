package com.arms.elasticsearch.util.query.bool;

import com.arms.elasticsearch.util.query.EsQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;


public abstract class Range extends EsBoolQuery {
	public abstract RangeQueryBuilder rangeQueryBuilder();

	@Override
	public EsQuery boolQueryBuilder(BoolQueryBuilder boolQueryBuilder){
		if(rangeQueryBuilder()!=null){
			boolQueryBuilder.filter(rangeQueryBuilder());
		}
		return this;
	}
}
