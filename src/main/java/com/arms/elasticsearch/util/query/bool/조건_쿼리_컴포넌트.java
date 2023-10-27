package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.BoolQueryBuilder;

public abstract class 조건_쿼리_컴포넌트 {

	BoolQueryBuilder boolQueryBuilder;


	public BoolQueryBuilder getBoolQuery(){
		return boolQueryBuilder;
	}

}
