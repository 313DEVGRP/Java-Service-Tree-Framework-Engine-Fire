package com.arms.egovframework.javaservice.esframework.esquery;

import com.arms.egovframework.javaservice.esframework.EsQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;

public abstract class EsBoolQuery extends EsQuery {

	public abstract void boolQueryBuilder(BoolQueryBuilder boolQueryBuilder);

}
