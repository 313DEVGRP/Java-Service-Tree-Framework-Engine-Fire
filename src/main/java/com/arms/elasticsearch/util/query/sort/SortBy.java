package com.arms.elasticsearch.util.query.sort;

import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.arms.elasticsearch.util.query.EsQuery;
import com.arms.elasticsearch.util.query.정렬_요청;

public class SortBy extends EsQuery {

	public List<FieldSortBuilder> fieldSortBuilderList;

	public SortBy(List<정렬_요청> 정렬_요청들){
		this.fieldSortBuilderList = 정렬_요청들.stream()
			.map(정렬_요청 -> SortBuilders.fieldSort(정렬_요청.get필드()).order(SortOrder.fromString(정렬_요청.get정렬기준())))
			.collect(Collectors.toList());
	};

	public List<FieldSortBuilder> sortBy() {
		return this.fieldSortBuilderList;
	};

}
