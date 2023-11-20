package com.arms.elasticsearch.util.query.sort;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.arms.elasticsearch.util.query.EsQuery;

public class SortBy extends EsQuery {

	private Sort sort;

	public SortBy(String field ,String sortOrder){
		this.sort = Sort.by(Direction.fromString(sortOrder),field);
	};

	public Sort sortQuery() {
		return this.sort;
	};
}
