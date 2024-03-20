package com.arms.elasticsearch.query.esquery;

import java.util.List;
import java.util.stream.Collectors;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.기본_정렬_요청;

public class EsSortQuery extends EsQuery {

	public List<FieldSortBuilder> fieldSortBuilderList;

	public EsSortQuery(List<기본_정렬_요청> 기본정렬_요청들){
		this.fieldSortBuilderList = 기본정렬_요청들.stream()
			.map(기본_정렬_요청 -> SortBuilders.fieldSort(기본_정렬_요청.get필드()).order(SortOrder.fromString(기본_정렬_요청.get정렬기준())))
			.collect(Collectors.toList());
	};

	public List<FieldSortBuilder> sortBy() {
		return this.fieldSortBuilderList;
	};

}
