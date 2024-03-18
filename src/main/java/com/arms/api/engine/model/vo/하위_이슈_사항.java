package com.arms.api.engine.model.vo;

import static java.util.stream.Collectors.*;

import java.util.List;

import com.arms.elasticsearch.검색결과;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(exclude = "하위_이슈_사항_담당자" )
@AllArgsConstructor
@Getter
public class 하위_이슈_사항 {
	private final String 요구_사항_번호;
	private final List<String> 하위_이슈_사항_담당자;
	public static 하위_이슈_사항 empty(){
		return new 하위_이슈_사항(null,null);
	}

	public static 하위_이슈_사항 요구_사항_번호_생성(String 요구_사항_번호){
		return new 하위_이슈_사항(요구_사항_번호,null);
	}

	public 하위_이슈_사항(검색결과 검색결과){
		this.요구_사항_번호 = 검색결과.get필드명();
		this.하위_이슈_사항_담당자 = 검색결과.get하위검색결과()
			.entrySet()
			.stream()
			.flatMap(a -> a.getValue().stream())
			.map(a -> a.get필드명())
			.collect(toList());
	}

}
