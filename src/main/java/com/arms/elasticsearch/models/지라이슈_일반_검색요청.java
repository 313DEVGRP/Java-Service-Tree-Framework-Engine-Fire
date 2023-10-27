package com.arms.elasticsearch.models;

import com.arms.elasticsearch.util.base.검색_기본_요청;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 지라이슈_일반_검색요청 extends 검색_기본_요청 {
	private Boolean isReq;
}
