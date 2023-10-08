package com.arms.elasticsearch.util;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class 검색결과_목록 {

	final List<검색결과> 목록;

	public 검색결과_목록(List<검색결과> 목록) {
		this.목록 = 목록;
	}

	public 검색결과_목록 중복제거(){
		return new 검색결과_목록(목록.stream().distinct().collect(toList()));
	}

}
