package com.arms.elasticsearch.query.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public abstract class 평균_및_최대치_집계_요청 extends 기본_검색_요청 {
	private List<String> 그룹_필드들;
}
