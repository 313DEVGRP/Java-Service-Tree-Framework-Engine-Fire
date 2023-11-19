package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;

public class TermQueryMust extends Must {

	private TermQueryBuilder termQueryBuilder;

	public TermQueryMust (String name, String value){
		this.termQueryBuilder = QueryBuilders.termQuery(name, value);
	}

	public TermQueryMust (String name, Long value){
		if(name!=null&&value!=null){
			this.termQueryBuilder = QueryBuilders.termQuery(name, value);
		}

	}

	public TermQueryMust (String name, Boolean value){
		if(name!=null&&value!=null){
			this.termQueryBuilder = QueryBuilders.termQuery(name, value);
		}
	}

	public TermQueryBuilder termQueryBuilder() {
		return termQueryBuilder;
	}
}
