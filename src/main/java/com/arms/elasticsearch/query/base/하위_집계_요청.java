package com.arms.elasticsearch.query.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class 하위_집계_요청 extends 기본_집계_요청 {
	private List<String> 하위그룹필드들 = new ArrayList<>();
	private List<하위_집계> _하위_집계_필드들 = new ArrayList<>();
	public List<하위_집계> to_하위_집계_필드들(){
		return 하위그룹필드들.stream()
				.map(a-> 하위_집계.builder().필드명(a).build())
				.collect(Collectors.toList());
	}
}
