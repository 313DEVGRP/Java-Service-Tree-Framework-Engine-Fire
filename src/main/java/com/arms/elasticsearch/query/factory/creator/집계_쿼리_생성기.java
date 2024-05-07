package com.arms.elasticsearch.query.factory.creator;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.일반_집계_요청;
import com.arms.elasticsearch.query.factory.creator.query.일반_집계_쿼리;
import com.arms.elasticsearch.query.factory.creator.query.집계_쿼리;
import com.arms.elasticsearch.query.쿼리_생성기;

import lombok.Setter;

@Setter
public class 집계_쿼리_생성기 implements 쿼리_생성기 {

	private final 집계_쿼리 _집계_쿼리;

	private 집계_쿼리_생성기(집계_쿼리 _집계_쿼리){
		this._집계_쿼리 = _집계_쿼리;
	}

	public static 쿼리_생성기 of(일반_집계_요청 _일반_집계_쿼리, EsQuery esQuery){
		return new 집계_쿼리_생성기(일반_집계_쿼리.of(_일반_집계_쿼리,esQuery));
	}

	public static 쿼리_생성기 of( EsQuery esQuery){
		return new 집계_쿼리_생성기( 일반_집계_쿼리.of(esQuery));
	}

	@Override
	public NativeSearchQuery 생성() {
		return _집계_쿼리.생성();
	}

}
