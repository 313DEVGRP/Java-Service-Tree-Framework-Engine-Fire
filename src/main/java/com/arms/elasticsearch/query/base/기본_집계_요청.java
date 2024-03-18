package com.arms.elasticsearch.query.base;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class 기본_집계_요청 extends 기본_검색_요청 {

	private List<String> 하위그룹필드들;
	private String 메인그룹필드;
	private boolean 컨텐츠보기여부;
	private int 하위크기 = 1000;

}
