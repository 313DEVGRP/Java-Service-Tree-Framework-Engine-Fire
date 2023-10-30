package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.BoolQueryBuilder;

public abstract class 조건_쿼리_컴포넌트_요소 extends 조건_쿼리_컴포넌트 {

	protected 조건_쿼리_컴포넌트 조건_쿼리_컴포넌트;

	public abstract BoolQueryBuilder getBoolQuery();
}
