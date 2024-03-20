package com.arms.elasticsearch.query.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class 기본_정렬_요청 {

	private String 필드;
	private String 정렬기준;
}
