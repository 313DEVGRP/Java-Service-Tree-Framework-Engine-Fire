package com.arms.egovframework.javaservice.esframework.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class 기본_검색_집계_하위_요청 extends 기본_검색_집계_요청 {
	private List<String> 하위그룹필드들 = new ArrayList<>();
	private List<집계_하위_요청> 집계_하위_요청_필드들 = new ArrayList<>();
	public List<집계_하위_요청> to_하위_집계_필드들(){
		return 하위그룹필드들.stream()
				.map(a-> 집계_하위_요청.builder().하위_필드명(a).build())
				.collect(Collectors.toList());
	}
}
