package com.arms.elasticsearch.util.query.query_string;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

import com.arms.elasticsearch.util.query.EsQuery;

public class QueryString extends EsQuery {

	private QueryStringQueryBuilder queryBuilder;

	public QueryString(String queryString ){
		if(queryString!=null){
			this.queryBuilder = QueryBuilders.queryStringQuery(queryString);
		}
	};

	public QueryStringQueryBuilder queryString() {
		return this.queryBuilder;
	};
}
