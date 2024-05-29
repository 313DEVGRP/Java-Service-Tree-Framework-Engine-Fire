package com.arms.egovframework.javaservice.esframework;

import com.arms.egovframework.javaservice.esframework.esquery.EsBoolQuery;
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
