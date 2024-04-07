package com.arms.elasticsearch.query.filter;


import com.arms.elasticsearch.query.Filter;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TermsQueryFilter extends Filter<TermsQueryBuilder> {

	private TermsQueryBuilder termsQueryBuilder;

	public TermsQueryFilter(String name, String value){
		if(name!=null&&value!=null){
			this.termsQueryBuilder = QueryBuilders.termsQuery(name, value);
		}
	}

	public TermsQueryFilter(String name, List<?> value){
		if(name!=null&&value!=null){
			this.termsQueryBuilder = QueryBuilders.termsQuery(name, value);
		}
	}

	public TermsQueryFilter(String name, Long value){
		if(name!=null&&value!=null){
			this.termsQueryBuilder = new TermsQueryFilter(name, List.of(value)).termsQueryBuilder;
		}
	}

	public TermsQueryFilter(String name, boolean value){
		if(name!=null){
			this.termsQueryBuilder = QueryBuilders.termsQuery(name, value);
		}
	}

	public TermsQueryFilter(String name, Long[] value){
		if(name!=null&&value!=null){
			this.termsQueryBuilder = QueryBuilders.termsQuery(name
				, Arrays.stream(value).collect(Collectors.toList()));
		}
	}

	@Override
	public AbstractQueryBuilder<TermsQueryBuilder> abstractQueryBuilder() {
		return  termsQueryBuilder;
	}

}
