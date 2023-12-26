package com.arms.api.engine.models;

import com.arms.elasticsearch.util.base.기본_집계_요청;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 지라이슈_일반_집계_요청 extends 기본_집계_요청 {
	private Boolean isReq;
}
