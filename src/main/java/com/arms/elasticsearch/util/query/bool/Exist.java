package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.BoolQueryBuilder;
import com.arms.elasticsearch.util.query.EsQuery;
import org.elasticsearch.index.query.ExistsQueryBuilder;


public abstract class Exist extends EsBoolQuery {
	public abstract ExistsQueryBuilder existsQueryBuilder();

	@Override
	public EsQuery boolQueryBuilder(BoolQueryBuilder boolQueryBuilder){
		if(existsQueryBuilder()!=null){
			boolQueryBuilder.filter(existsQueryBuilder());
		}
		return this;
	}
}
