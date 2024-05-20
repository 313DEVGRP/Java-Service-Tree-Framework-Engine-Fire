package com.arms.elasticsearch.query.factory.creator;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.하위_집계_요청;
import com.arms.elasticsearch.query.factory.creator.query.일반_집계_쿼리;
import com.arms.elasticsearch.query.factory.creator.query.일자별_집계_쿼리;
import com.arms.elasticsearch.query.factory.creator.query.집계_쿼리;
import lombok.Setter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

@Setter
public class 하위_계층_집계_쿼리_생성기 implements 쿼리_생성기 {
	private final 집계_쿼리 _집계_쿼리;

	private 하위_계층_집계_쿼리_생성기(집계_쿼리 _집계_쿼리){
		this._집계_쿼리 = _집계_쿼리;
	}

	public static 쿼리_생성기 of(하위_집계_요청 하위_집계_요청, EsQuery esQuery){
		return new 하위_계층_집계_쿼리_생성기(일반_집계_쿼리.of(하위_집계_요청, esQuery));
	}

	public static 쿼리_생성기 week(하위_집계_요청 하위_집계_요청, EsQuery esQuery){
		return new 하위_계층_집계_쿼리_생성기(일자별_집계_쿼리.week(하위_집계_요청, esQuery));
	}

	public static 쿼리_생성기 day(하위_집계_요청 하위_집계_요청, EsQuery esQuery){
		return new 하위_계층_집계_쿼리_생성기(일자별_집계_쿼리.day(하위_집계_요청, esQuery));
	}

	@Override
	public NativeSearchQuery 생성() {
		_집계_쿼리.계층_하위_집계_빌더_적용();
		return _집계_쿼리.생성();
	}

}
