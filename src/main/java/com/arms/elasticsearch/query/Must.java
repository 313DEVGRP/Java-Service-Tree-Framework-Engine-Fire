package com.arms.elasticsearch.query;

import com.arms.elasticsearch.query.esquery.EsBoolQuery;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;

public abstract class Must<T extends AbstractQueryBuilder<T>>  extends EsBoolQuery {
	public abstract  AbstractQueryBuilder<T> abstractQueryBuilder();

	@Override
	public void boolQueryBuilder(BoolQueryBuilder boolQueryBuilder){
		if(abstractQueryBuilder()!=null){
			boolQueryBuilder.must(abstractQueryBuilder());
		}
	}
}
