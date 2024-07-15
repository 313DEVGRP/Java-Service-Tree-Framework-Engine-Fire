package com.arms.egovframework.javaservice.esframework.test;



import static com.arms.egovframework.javaservice.esframework.test.FilterBuilder.*;
import static com.arms.egovframework.javaservice.esframework.test.AggregationBuilder.*;

import java.util.function.Consumer;

import org.checkerframework.checker.units.qual.A;

public class QueryBuilder {


	public QueryBuilder query(Filter... filter){
		return this;
	}

	public QueryBuilder aggregation(AggregationTermsBuilder... aggregationTermsBuilders){
		return this;
	}

	public static void main(String[] args) {

		QueryBuilder aggregation = new QueryBuilder().query(
			filter(
				term(
					a -> a.field("순대").value("떡볶이")
				),
				term(
					a -> a.field("김치").value("볶음밥")
				),
				terms(
					a -> a.field("김치").value("볶음밥", "라면")
				)
			)
		).aggregation(
			aggrTerms(
				a -> a.name("그룹")
			)
		);

	}

}
