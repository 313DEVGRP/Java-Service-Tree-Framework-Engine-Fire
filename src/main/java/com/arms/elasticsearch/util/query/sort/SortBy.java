package com.arms.elasticsearch.util.query.sort;

import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.arms.elasticsearch.util.query.EsQuery;

public class SortBy extends EsQuery {

	private FieldSortBuilder sort;

	public SortBy(String field ,String sortOrder){
		this.sort
			= SortBuilders.fieldSort(field).order(SortOrder.fromString(sortOrder));
	};

	public FieldSortBuilder sortQuery() {
		return this.sort;
	};
}
