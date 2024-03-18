package com.arms.api.engine.model;

import java.util.List;

import com.arms.elasticsearch.base.기본_집계_요청;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 지라이슈_일자별_집계_요청 extends 기본_집계_요청 {
	private Boolean isReq;
	private String 필터필드;
	private List<?> 필터필드검색어;
}
