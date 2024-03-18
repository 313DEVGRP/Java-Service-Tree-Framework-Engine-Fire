package com.arms.elasticsearch;

import static java.util.stream.Collectors.*;

import java.util.List;

import lombok.Getter;

@Getter
public class 검색결과_목록 {

	private final List<검색결과> 결과;

	public 검색결과_목록(List<검색결과> 목록) {
		this.결과 = 목록;
	}

	public 검색결과_목록 중복제거(){
		return new 검색결과_목록(결과.stream().distinct().collect(toList()));
	}



}
