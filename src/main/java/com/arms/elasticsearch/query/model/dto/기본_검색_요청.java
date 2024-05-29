package com.arms.elasticsearch.query.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class 기본_검색_요청 {
	private String 검색어;
	private List<String> 검색_필드들;
	private int 크기 = 1000;
	private int 페이지 = 0;
	private boolean 페이지_처리_여부 = true;
}
