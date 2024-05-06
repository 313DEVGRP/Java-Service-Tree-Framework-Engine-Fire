package com.arms.elasticsearch.query.factory;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.기본_집계_요청;
import com.arms.elasticsearch.query.factory.query.일반_집계_쿼리;
import com.arms.elasticsearch.query.쿼리_생성기;
import lombok.Setter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

@Setter
public class 계층_집계_쿼리_생성기 implements 쿼리_생성기 {
	private final 일반_집계_쿼리 일반_집계_쿼리;

	private 계층_집계_쿼리_생성기(기본_집계_요청 기본_집계_요청, EsQuery esQuery){
		this.일반_집계_쿼리 = new 일반_집계_쿼리(기본_집계_요청, esQuery);
	}

	public static 쿼리_생성기 of(기본_집계_요청 기본_집계_요청, EsQuery esQuery){
		return new 계층_집계_쿼리_생성기(기본_집계_요청, esQuery);
	}

	@Override
	public NativeSearchQuery 생성() {
		일반_집계_쿼리.계층_하위_집계_빌더_적용();
		return 일반_집계_쿼리.생성();
	}

}
