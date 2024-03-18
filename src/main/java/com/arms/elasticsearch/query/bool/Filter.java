package com.arms.elasticsearch.query.bool;

import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;

public abstract class Filter<T extends AbstractQueryBuilder<T>> extends EsBoolQuery {

	public abstract  AbstractQueryBuilder<T> abstractQueryBuilder();

	@Override
	public void boolQueryBuilder(BoolQueryBuilder boolQueryBuilder){
		if(abstractQueryBuilder()!=null){
			boolQueryBuilder.filter(abstractQueryBuilder());
		}
	}


}
