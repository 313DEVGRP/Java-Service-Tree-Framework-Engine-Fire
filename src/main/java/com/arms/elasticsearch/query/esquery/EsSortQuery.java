package com.arms.elasticsearch.query.esquery;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.정렬_필드_지정;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.List;
import java.util.stream.Collectors;

public class EsSortQuery extends EsQuery {

	public List<FieldSortBuilder> fieldSortBuilderList;

	public EsSortQuery(List<정렬_필드_지정> 기본정렬_요청들){
		this.fieldSortBuilderList = 기본정렬_요청들.stream()
			.map(일반_검색_요청 -> SortBuilders.fieldSort(일반_검색_요청.get필드()).order(SortOrder.fromString(일반_검색_요청.get정렬기준())))
			.collect(Collectors.toList());
	};

	public List<FieldSortBuilder> sortBy() {
		return this.fieldSortBuilderList;
	};

}
