package com.arms.elasticsearch.util;

import java.util.ArrayList;
import java.util.List;

public class 검색결과_목록 {

	final List<검색결과> 목록 = new ArrayList<>();

	public void 중복시_기존_목록_삭제_추가(검색결과 검색결과){
		if(목록.contains(검색결과)){
			목록.remove(검색결과);
		}

		목록.add(검색결과);
	}

	public void 중복허용_추가(검색결과 검색결과){
		목록.add(검색결과);
	}
}
