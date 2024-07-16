package com.arms.egovframework.javaservice.esframework.test;

public class AggregationTermsBuilder implements TermBuilderAbstract{
	private final AggregationTerms aggregationTerms = new AggregationTerms();

	public AggregationTerms aggregationTerms(){
		return this.aggregationTerms;
	}

	public AggregationTermsBuilder name(String name){
		aggregationTerms.setName(name);
		return this;
	}

	public AggregationTermsBuilder field(String field){
		aggregationTerms.setField(field);
		return this;
	}


}
