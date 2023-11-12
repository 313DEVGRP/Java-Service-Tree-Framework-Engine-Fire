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
public class 서비스_조건 extends 조건_쿼리_컴포넌트_요소 {

	private Long pdServiceId;
	private 조건_쿼리_컴포넌트 조건_쿼리_컴포넌트;

	@Override
	public BoolQueryBuilder getBoolQuery() {
		super.조건_쿼리_컴포넌트 = this.조건_쿼리_컴포넌트;
		if(!ObjectUtils.isEmpty(pdServiceId)){
			return this.조건_쿼리_컴포넌트.getBoolQuery()
				.must(QueryBuilders.termQuery("pdServiceId", pdServiceId));
		}
		return this.조건_쿼리_컴포넌트.getBoolQuery();
	}

}
