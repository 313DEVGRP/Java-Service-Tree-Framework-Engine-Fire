package com.arms.api.engine.models.boolquery;

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.arms.elasticsearch.util.query.bool.조건_쿼리_컴포넌트;
import com.arms.elasticsearch.util.query.bool.조건_쿼리_컴포넌트_요소;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class 진행사항_필터_조건 extends 조건_쿼리_컴포넌트_요소 {

	private String 필터필드;
	private List<?> 필터필드검색어;

	private 조건_쿼리_컴포넌트 조건_쿼리_컴포넌트;

	public BoolQueryBuilder getBoolQuery(){
		super.조건_쿼리_컴포넌트 = this.조건_쿼리_컴포넌트;
		if(!ObjectUtils.isEmpty(필터필드)){
			return this.조건_쿼리_컴포넌트.getBoolQuery().filter(QueryBuilders.termsQuery(필터필드, 필터필드검색어));
		}
		return this.조건_쿼리_컴포넌트.getBoolQuery();
	}

}
