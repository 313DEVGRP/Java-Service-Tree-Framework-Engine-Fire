package com.arms.elasticsearch.query.factory.creator;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.기본_검색_집계_하위_요청;
import com.arms.elasticsearch.query.factory.creator.query.일반_집계_쿼리;
import com.arms.elasticsearch.query.factory.creator.query.일자별_집계_쿼리;
import com.arms.elasticsearch.query.factory.creator.query.집계_쿼리;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

@Setter
@Getter
public class 중첩_집계_단일_쿼리_생성기 implements 쿼리_생성기 {

	private final 집계_쿼리 _집계_쿼리;

	private 중첩_집계_단일_쿼리_생성기(집계_쿼리 집계_쿼리){
		this._집계_쿼리 = 집계_쿼리;
	}

	public static 쿼리_생성기 of(기본_검색_집계_하위_요청 하위_집계_요청, EsQuery esQuery){
		return new 중첩_집계_단일_쿼리_생성기(일반_집계_쿼리.of(하위_집계_요청, esQuery));
	}

	public static 쿼리_생성기 week(기본_검색_집계_하위_요청 하위_집계_요청, EsQuery esQuery){
		return new 중첩_집계_단일_쿼리_생성기(일자별_집계_쿼리.week(하위_집계_요청, esQuery));
	}

	public static 쿼리_생성기 week(EsQuery esQuery){
		return new 중첩_집계_단일_쿼리_생성기(일자별_집계_쿼리.week(esQuery));
	}

	public static 쿼리_생성기 day(기본_검색_집계_하위_요청 하위_집계_요청, EsQuery esQuery){
		return new 중첩_집계_단일_쿼리_생성기(일자별_집계_쿼리.day(하위_집계_요청, esQuery));
	}

	@Override
	public NativeSearchQuery 생성() {
		_집계_쿼리.형제_하위_집계_빌더_적용();
		return _집계_쿼리.생성();
	}
}
