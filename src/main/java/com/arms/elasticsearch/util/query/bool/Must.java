package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;

public abstract class Must implements EsQuery{
	public abstract TermQueryBuilder termQueryBuilder();

	@Override
	public EsQuery boolQueryBuilder(BoolQueryBuilder boolQueryBuilder){

		if(termQueryBuilder()!=null){
			boolQueryBuilder.must(termQueryBuilder());
		}
		return this;
	}
}
