package com.arms.egovframework.javaservice.esframework.must;

import com.arms.egovframework.javaservice.esframework.Must;
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
