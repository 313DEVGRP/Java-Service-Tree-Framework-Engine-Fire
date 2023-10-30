package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.QueryBuilders;

import lombok.Builder;

@Builder
public class 조건_쿼리_생성자 extends 조건_쿼리_컴포넌트 {

	public 조건_쿼리_생성자(){
		this.boolQueryBuilder = QueryBuilders.boolQuery();
	}
}
