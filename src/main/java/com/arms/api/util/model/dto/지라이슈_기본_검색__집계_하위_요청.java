package com.arms.api.util.model.dto;

import com.arms.api.util.model.enums.IsReqType;
import com.arms.elasticsearch.query.base.기본_검색_집계_하위_요청;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class 지라이슈_기본_검색__집계_하위_요청 extends 기본_검색_집계_하위_요청 {

	private IsReqType isReqType;
	private Boolean isReq;
	private List<Long> pdServiceVersionLinks;

}
