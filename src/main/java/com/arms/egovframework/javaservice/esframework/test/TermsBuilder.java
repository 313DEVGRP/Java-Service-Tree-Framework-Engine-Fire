package com.arms.egovframework.javaservice.esframework.test;

import java.util.List;

public class TermsBuilder implements TermBuilderAbstract{

	private final Terms terms = new Terms();

	public Terms terms(){
		return this.terms;
	}

	public TermsBuilder field(String field){
		terms.setField(field);
		return this;
	}

	public TermsBuilder value(String... value){
		terms.setValue(List.of(value));
		return this;
	}
}
