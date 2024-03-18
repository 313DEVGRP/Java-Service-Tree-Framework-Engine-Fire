package com.arms.elasticsearch.query.bool;

import com.arms.elasticsearch.query.EsQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;

public abstract class EsBoolQuery extends EsQuery {

	public abstract void boolQueryBuilder(BoolQueryBuilder boolQueryBuilder);

}
