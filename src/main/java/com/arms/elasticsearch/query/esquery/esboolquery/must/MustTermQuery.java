package com.arms.elasticsearch.query.esquery.esboolquery.must;

import com.arms.elasticsearch.query.esquery.esboolquery.Must;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;

public class MustTermQuery extends Must<TermQueryBuilder> {

	private TermQueryBuilder termQueryBuilder;

	public MustTermQuery(String name, String value){
		if(name!=null&&value!=null){
			this.termQueryBuilder = QueryBuilders.termQuery(name, value);
		}
	}

	public MustTermQuery(String name, Long value){
		if(name!=null&&value!=null){
			this.termQueryBuilder = QueryBuilders.termQuery(name, value);
		}
	}

	public MustTermQuery(String name, Boolean value){
		if(name!=null&&value!=null){
			this.termQueryBuilder = QueryBuilders.termQuery(name, value);
		}
	}

	@Override
	public AbstractQueryBuilder<TermQueryBuilder> abstractQueryBuilder() {
		return termQueryBuilder;
	}
}
