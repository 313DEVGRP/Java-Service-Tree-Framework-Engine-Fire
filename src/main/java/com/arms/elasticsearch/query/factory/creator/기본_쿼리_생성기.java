package com.arms.elasticsearch.query.factory.creator;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.기본_검색_요청;
import com.arms.elasticsearch.query.base.기본_검색_집계_요청;
import com.arms.elasticsearch.query.factory.creator.query.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

@Setter
@Getter
public class 기본_쿼리_생성기 implements 쿼리_생성기 {

	private final 쿼리_생성기 쿼리_생성기;

	private 기본_쿼리_생성기(기본_쿼리 기본_쿼리) {
		this.쿼리_생성기 = 기본_쿼리;
	}
	private 기본_쿼리_생성기(집계_쿼리 집계_쿼리){
		this.쿼리_생성기 = 집계_쿼리;
	}

	public static 쿼리_생성기 기본검색(기본_검색_요청 기본_검색_요청, EsQuery esQuery){
		return new 기본_쿼리_생성기(기본_검색_쿼리.of(기본_검색_요청, esQuery));
	}

	public static 쿼리_생성기 집계검색(기본_검색_집계_요청 기본_검색_집계_요청, EsQuery esQuery){
		return new 기본_쿼리_생성기(기본_집계_쿼리.of(기본_검색_집계_요청,esQuery));
	}

	@Override
	public NativeSearchQuery 생성() {
		return 쿼리_생성기.생성();
	}
}
