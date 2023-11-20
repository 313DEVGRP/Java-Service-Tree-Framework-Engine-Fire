package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;

import com.arms.elasticsearch.util.query.EsQuery;

public abstract class Filter extends EsBoolQuery {

	public abstract TermsQueryBuilder termsQueryBuilder();

	@Override
	public EsQuery boolQueryBuilder(BoolQueryBuilder boolQueryBuilder){
		if(termsQueryBuilder()!=null){
			boolQueryBuilder.filter(termsQueryBuilder());
		}
		return this;
	}
}
