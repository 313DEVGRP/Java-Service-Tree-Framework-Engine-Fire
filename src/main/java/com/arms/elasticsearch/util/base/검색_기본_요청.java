package com.arms.elasticsearch.util.base;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class 검색_기본_요청 {

	private List<String> 하위그룹필드들;
	private String 메인그룹필드;
	private int 크기 = 1000;
	private boolean 컨텐츠보기여부;

}
