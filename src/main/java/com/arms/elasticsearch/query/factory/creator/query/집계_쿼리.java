package com.arms.elasticsearch.query.factory.creator.query;

public interface 집계_쿼리 extends 쿼리_생성기 {
	void 계층_하위_집계_빌더_적용();
	void 형제_하위_집계_빌더_적용();
	void 형제_하위_메트릭_집계_빌더_적용();
}
