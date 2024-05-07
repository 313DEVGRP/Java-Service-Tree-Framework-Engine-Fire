package com.arms.elasticsearch.query.base;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class 하위_집계_요청 extends 일반_집계_요청 {
	private List<String> 하위그룹필드들 = new ArrayList<>();
}
