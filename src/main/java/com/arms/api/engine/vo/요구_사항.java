package com.arms.api.engine.vo;

import java.util.List;

import com.arms.elasticsearch.util.검색결과;

public class 요구_사항 {

	private final String 요구_사항_번호;
	private final String 요구_사항_담당자;
	private final int 작업자수;
	private final List<String> 하위_이슈_사항_담당자;

	public 요구_사항(검색결과 검색결과,하위_이슈_사항들 하위_이슈_사항들){
		하위_이슈_사항 하위_이슈_사항 = 하위_이슈_사항들.findBy요구사항번호(검색결과.get필드명());
		this.하위_이슈_사항_담당자 = 하위_이슈_사항.get하위_이슈_사항_담당자();
		this.요구_사항_번호 = 검색결과.get필드명();
		this.요구_사항_담당자 = 검색결과.get하위검색결과()
			.entrySet()
			.stream()
			.flatMap(a -> a.getValue().stream())
			.findFirst()
			.map(a-> a.get필드명())
			.orElseGet(()->"N/A");

		this.작업자수 = 검색결과.get하위검색결과()
			.entrySet()
			.stream()
			.flatMap(a -> a.getValue().stream())
			.findFirst()
			.map(a-> {
				int 임시작업자수 = 1;
				if(하위_이슈_사항.get하위_이슈_사항_담당자()!=null){
					임시작업자수 += 하위_이슈_사항.get하위_이슈_사항_담당자().size();
					if(하위_이슈_사항.get하위_이슈_사항_담당자().contains(a.get필드명())){
						return 임시작업자수-1;
					};
				}
				return 임시작업자수;
			})
			.orElse(0);
	}
}
