package com.arms.egovframework.javaservice.esframework.test;

public class TermBuilder implements TermBuilderAbstract{
	private final Term term = new Term();

	public Term term(){
		return this.term;
	}

	public TermBuilder field(String field){
		term.setField(field);
		return this;
	}

	public TermBuilder value(String value){
		term.setValue(value);
		return this;
	}
}
