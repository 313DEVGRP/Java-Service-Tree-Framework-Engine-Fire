package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

public abstract class Filter implements EsQuery {

	public abstract TermsQueryBuilder termsQueryBuilder();

	@Override
	public EsQuery boolQueryBuilder(BoolQueryBuilder boolQueryBuilder){
		if(termsQueryBuilder()!=null){
			boolQueryBuilder.filter(termsQueryBuilder());
		}
		return this;
	}
}
