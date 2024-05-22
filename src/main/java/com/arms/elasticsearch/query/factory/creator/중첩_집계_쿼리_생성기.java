package com.arms.elasticsearch.query.factory.creator;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.기본_검색_집계_하위_요청;
import com.arms.elasticsearch.query.factory.creator.query.기본_집계_쿼리;
import com.arms.elasticsearch.query.factory.creator.query.일자별_집계_쿼리;
import com.arms.elasticsearch.query.factory.creator.query.집계_쿼리;
import com.arms.elasticsearch.query.factory.creator.query.쿼리_생성기;
import lombok.Setter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

@Setter
public class 중첩_집계_쿼리_생성기 implements 쿼리_생성기 {

	private final 집계_쿼리 집계_쿼리;

	private 중첩_집계_쿼리_생성기(집계_쿼리 집계_쿼리){
		this.집계_쿼리 = 집계_쿼리;
	}

	public static 쿼리_생성기 포괄(기본_검색_집계_하위_요청 기본_검색_집계_하위_요청, EsQuery esQuery){
		중첩_집계_쿼리_생성기 중첩_집계_쿼리_생성기 = new 중첩_집계_쿼리_생성기(기본_집계_쿼리.of(기본_검색_집계_하위_요청, esQuery));
		중첩_집계_쿼리_생성기.계층_하위_집계_빌더_적용();
		return 중첩_집계_쿼리_생성기;
	}

	public static 쿼리_생성기 포괄_주별(기본_검색_집계_하위_요청 기본_검색_집계_하위_요청, EsQuery esQuery){
		중첩_집계_쿼리_생성기 중첩_집계_쿼리_생성기 = new 중첩_집계_쿼리_생성기(일자별_집계_쿼리.week(기본_검색_집계_하위_요청, esQuery));
		중첩_집계_쿼리_생성기.계층_하위_집계_빌더_적용();
		return 중첩_집계_쿼리_생성기;
	}

	public static 쿼리_생성기 포괄_일별(기본_검색_집계_하위_요청 기본_검색_집계_하위_요청, EsQuery esQuery){
		중첩_집계_쿼리_생성기 중첩_집계_쿼리_생성기 = new 중첩_집계_쿼리_생성기(일자별_집계_쿼리.day(기본_검색_집계_하위_요청, esQuery));
		중첩_집계_쿼리_생성기.계층_하위_집계_빌더_적용();
		return 중첩_집계_쿼리_생성기;
	}

	public static 쿼리_생성기 단일(기본_검색_집계_하위_요청 기본_검색_집계_하위_요청, EsQuery esQuery){
		중첩_집계_쿼리_생성기 중첩_집계_쿼리_생성기 = new 중첩_집계_쿼리_생성기(기본_집계_쿼리.of(기본_검색_집계_하위_요청, esQuery));
		중첩_집계_쿼리_생성기.형제_하위_집계_빌더_적용();
		return 중첩_집계_쿼리_생성기;
	}

	public static 쿼리_생성기 단일_주별(기본_검색_집계_하위_요청 기본_검색_집계_하위_요청, EsQuery esQuery){
		중첩_집계_쿼리_생성기 중첩_집계_쿼리_생성기 = new 중첩_집계_쿼리_생성기(일자별_집계_쿼리.week(기본_검색_집계_하위_요청, esQuery));
		중첩_집계_쿼리_생성기.형제_하위_집계_빌더_적용();
		return 중첩_집계_쿼리_생성기;
	}

	public static 쿼리_생성기 단일_일별(기본_검색_집계_하위_요청 기본_검색_집계_하위_요청, EsQuery esQuery){
		중첩_집계_쿼리_생성기 중첩_집계_쿼리_생성기 = new 중첩_집계_쿼리_생성기(일자별_집계_쿼리.day(기본_검색_집계_하위_요청, esQuery));
		중첩_집계_쿼리_생성기.형제_하위_집계_빌더_적용();
		return 중첩_집계_쿼리_생성기;
	}

	public void 계층_하위_집계_빌더_적용(){
		집계_쿼리.계층_하위_집계_빌더_적용();
	}

	public void 형제_하위_집계_빌더_적용(){
		집계_쿼리.형제_하위_집계_빌더_적용();
	}

	@Override
	public NativeSearchQuery 생성() {
		return 집계_쿼리.생성();
	}

}
