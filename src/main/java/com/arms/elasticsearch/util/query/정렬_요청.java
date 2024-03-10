package com.arms.elasticsearch.util.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class 정렬_요청 {

	private String 필드;
	private String 정렬기준;
}
