package com.arms.egovframework.javaservice.esframework.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class 기본_검색_집계_요청 extends 기본_검색_요청 {
	private String 메인그룹필드;
	private boolean 컨텐츠보기여부;
	private int 하위크기 = 1000;
	private boolean 결과_갯수_기준_오름차순 = false;

}
