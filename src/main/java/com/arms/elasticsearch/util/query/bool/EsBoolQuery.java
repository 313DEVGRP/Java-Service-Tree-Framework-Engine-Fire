package com.arms.elasticsearch.util.query.bool;

import com.arms.elasticsearch.util.query.EsQuery;
import org.elasticsearch.index.query.BoolQueryBuilder;

public abstract class EsBoolQuery extends EsQuery {

	public abstract void boolQueryBuilder(BoolQueryBuilder boolQueryBuilder);

}
