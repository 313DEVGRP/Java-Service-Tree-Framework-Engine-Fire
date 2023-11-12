package com.arms.api.engine.models.boolquery;

import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.arms.elasticsearch.util.query.bool.조건_쿼리_컴포넌트;
import com.arms.elasticsearch.util.query.bool.조건_쿼리_컴포넌트_요소;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class 요구사항인지_여부_조건 extends 조건_쿼리_컴포넌트_요소 {

	private Boolean isReq;

	private 조건_쿼리_컴포넌트 조건_쿼리_컴포넌트;

	@Override
	public BoolQueryBuilder getBoolQuery() {
		super.조건_쿼리_컴포넌트 = this.조건_쿼리_컴포넌트;
		if(!ObjectUtils.isEmpty(isReq)){
			return this.조건_쿼리_컴포넌트.getBoolQuery()
				.must(QueryBuilders.termQuery("isReq", isReq));
		}
		return this.조건_쿼리_컴포넌트.getBoolQuery();
	}

}
