package com.arms.elasticsearch.util.query;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.data.domain.Sort;

public interface EsQuery {

	default BoolQueryBuilder boolQuery() {
		throw new RuntimeException();
	};

	default Sort sortQuery() {
		throw new RuntimeException();
	};


}
