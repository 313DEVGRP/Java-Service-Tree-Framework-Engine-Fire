package com.arms.egovframework.javaservice.esframework.test;

import java.util.ArrayList;
import java.util.List;

public class Aggregation {

	List<AggregationTerms> aggregationTermsList = new ArrayList<>();

	public void addFilter(AggregationTerms aggregationTerms){
		aggregationTermsList.add(aggregationTerms);
	}
}
