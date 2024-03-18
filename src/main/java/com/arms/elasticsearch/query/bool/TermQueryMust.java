package com.arms.elasticsearch.query.bool;

import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;

public class TermQueryMust extends Must<TermQueryBuilder> {

	private TermQueryBuilder termQueryBuilder;

	public TermQueryMust(String name, String value){
		if(name!=null&&value!=null){
			this.termQueryBuilder = QueryBuilders.termQuery(name, value);
		}
	}

	public TermQueryMust(String name, Long value){
		if(name!=null&&value!=null){
			this.termQueryBuilder = QueryBuilders.termQuery(name, value);
		}

	}

	public TermQueryMust(String name, Boolean value){
		if(name!=null&&value!=null){
			this.termQueryBuilder = QueryBuilders.termQuery(name, value);
		}
	}

	@Override
	public AbstractQueryBuilder<TermQueryBuilder> abstractQueryBuilder() {
		return termQueryBuilder;
	}
}
