package com.arms.elasticsearch.query.base;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class 일반_검색_요청 {
	private int 크기 = 1000;
	private int 페이지 = 0;
	private boolean 페이지_처리_여부 = true;
}
