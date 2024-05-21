package com.arms.elasticsearch.query.factory.creator.query;

import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;

public interface 집계_쿼리 {
	void 계층_하위_집계_빌더_적용();
	void 형제_하위_집계_빌더_적용();
	void 형제_하위_메트릭_집계_빌더_적용();
	NativeSearchQuery 생성();
}
