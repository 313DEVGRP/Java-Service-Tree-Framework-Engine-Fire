package com.arms.elasticsearch.query;

import com.arms.elasticsearch.query.EsQuery;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

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
