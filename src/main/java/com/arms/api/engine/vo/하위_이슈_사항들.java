package com.arms.api.engine.vo;

import static com.arms.api.engine.vo.하위_이슈_사항.*;

import java.util.List;

import lombok.Getter;

@Getter
public class 하위_이슈_사항들 {

	private final List<하위_이슈_사항> 하위_이슈_사항들;

	public 하위_이슈_사항들(List<하위_이슈_사항> 하위_이슈_사항들) {
		this.하위_이슈_사항들 = 하위_이슈_사항들;
	}

	public 하위_이슈_사항 findBy요구사항번호(String 요구_사항_번호){

		int 인덱스 = 하위_이슈_사항들.indexOf(요구_사항_번호_생성(요구_사항_번호));
		if(인덱스!=-1){
			return 하위_이슈_사항들.get(인덱스);
		}
		return empty();
	}

}
