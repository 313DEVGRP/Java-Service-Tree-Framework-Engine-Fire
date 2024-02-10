package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.BoolQueryBuilder;

import com.arms.elasticsearch.util.query.EsQuery;

public abstract class EsBoolQuery extends EsQuery {

	public abstract void boolQueryBuilder(BoolQueryBuilder boolQueryBuilder);

}
