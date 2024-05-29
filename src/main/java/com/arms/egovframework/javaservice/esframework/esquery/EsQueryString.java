package com.arms.egovframework.javaservice.esframework.esquery;

import com.arms.egovframework.javaservice.esframework.EsQuery;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

public class EsQueryString extends EsQuery {

	private QueryStringQueryBuilder queryBuilder;

	public EsQueryString(String queryString ){
		if(queryString!=null){
			this.queryBuilder = QueryBuilders.queryStringQuery(queryString);
		}
	};

	public QueryStringQueryBuilder queryString() {
		return this.queryBuilder;
	};
}
