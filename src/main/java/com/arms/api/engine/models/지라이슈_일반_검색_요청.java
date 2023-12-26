package com.arms.api.engine.models;

import com.arms.elasticsearch.util.base.기본_검색_요청;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class 지라이슈_일반_검색_요청 extends 기본_검색_요청 {
	private IsReqType isReqType;
	private List<Long> pdServiceVersionLinks;
}
