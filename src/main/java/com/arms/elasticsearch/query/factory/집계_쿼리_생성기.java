package com.arms.elasticsearch.query.factory;

import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.일반_집계_요청;
import com.arms.elasticsearch.query.factory.query.일반_집계_쿼리;
import com.arms.elasticsearch.query.쿼리_생성기;

import lombok.Setter;

@Setter
public class 집계_쿼리_생성기 implements 쿼리_생성기 {

	private final 일반_집계_쿼리 _일반_집계_쿼리;

	private 집계_쿼리_생성기(일반_집계_요청 _일반_집계_쿼리, EsQuery esQuery){
		this._일반_집계_쿼리 = 일반_집계_쿼리.of(_일반_집계_쿼리,esQuery);
	}

	public static 쿼리_생성기 of(일반_집계_요청 _일반_집계_쿼리, EsQuery esQuery){
		return new 집계_쿼리_생성기(_일반_집계_쿼리, esQuery);
	}

	@Override
	public NativeSearchQuery 생성() {
		return _일반_집계_쿼리.생성();
	}

}
