package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.BoolQueryBuilder;

public interface  EsQuery {

	default EsQuery boolQueryBuilder(BoolQueryBuilder boolQueryBuilder) {
		throw new RuntimeException();
	};

	default BoolQueryBuilder boolQuery() {
		throw new RuntimeException();
	};


}
