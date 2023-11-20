package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

import com.arms.elasticsearch.util.query.EsQuery;

public abstract class Must extends EsBoolQuery {
	public abstract TermQueryBuilder termQueryBuilder();

	@Override
	public EsQuery boolQueryBuilder(BoolQueryBuilder boolQueryBuilder){

		if(termQueryBuilder()!=null){
			boolQueryBuilder.must(termQueryBuilder());
		}
		return this;
	}
}
