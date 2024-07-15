package com.arms.egovframework.javaservice.esframework.test;

import java.util.function.Consumer;

public class AggregationBuilder {

	public static AggregationTermsBuilder aggrTerms(Consumer<AggregationTermsBuilder> consumers) {
		return buildTerms(consumers);
	}

	private static AggregationTermsBuilder buildTerms(Consumer<AggregationTermsBuilder> consumer) {
		AggregationTermsBuilder aggregationTermsBuilder = new AggregationTermsBuilder();
		consumer.accept(aggregationTermsBuilder);
		return aggregationTermsBuilder;
	}
}


