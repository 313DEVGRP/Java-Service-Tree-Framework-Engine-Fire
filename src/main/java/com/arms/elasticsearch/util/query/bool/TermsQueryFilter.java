package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;

import java.util.List;

public class TermsQueryFilter extends Filter<TermsQueryBuilder> {

	private TermsQueryBuilder termsQueryBuilder;

	public TermsQueryFilter(String name, String value){
		if(name!=null&&value!=null){
			this.termsQueryBuilder = QueryBuilders.termsQuery(name, value);
		}
	}

	public TermsQueryFilter(String name, List value){
		if(name!=null&&value!=null){
			this.termsQueryBuilder = QueryBuilders.termsQuery(name, value);
		}
	}

	public TermsQueryFilter(String name, Long[] value){
		if(name!=null&&value!=null){
			this.termsQueryBuilder = QueryBuilders.termsQuery(name, value);
		}
	}

	@Override
	public AbstractQueryBuilder<TermsQueryBuilder> abstractQueryBuilder() {
		return  termsQueryBuilder;
	}

}
