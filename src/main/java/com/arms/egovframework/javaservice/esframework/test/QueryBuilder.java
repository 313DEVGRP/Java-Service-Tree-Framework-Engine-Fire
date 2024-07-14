package com.arms.egovframework.javaservice.esframework.test;

import java.util.Arrays;
import java.util.function.Consumer;

public class QueryBuilder {

	public static Filter filter(TermBuilderAbstract... termBuilderAbstracts){

		Filter filter = new Filter();

		Arrays.stream(termBuilderAbstracts).forEach(
			builders->{
				if(builders instanceof TermBuilder){
					filter.addFilter(((TermBuilder)builders).term());
				}else if(builders instanceof TermsBuilder){
					filter.addFilter(((TermsBuilder)builders).terms());
				}
			}
		);

		return filter;
	}

	public static TermBuilderAbstract terms(Consumer<TermsBuilder> consumers) {
		return buildTerms(consumers);
	}

	private static TermsBuilder buildTerms( Consumer<TermsBuilder> consumer) {
		TermsBuilder termsBuilder = new TermsBuilder();
		consumer.accept(termsBuilder);
		return termsBuilder;
	}

	public static TermBuilderAbstract term(Consumer<TermBuilder> consumers) {
		return buildTerm(consumers);
	}

	private static TermBuilder buildTerm( Consumer<TermBuilder> consumer) {
		TermBuilder termBuilder = new TermBuilder();
		consumer.accept(termBuilder);
		return termBuilder;
	}

	public static void main(String[] args) {
		Filter filter = QueryBuilder.filter(
			term(
				a -> a.field("순대").value("떡볶이")
			),
			term(
				a -> a.field("김치").value("볶음밥")
			),
			terms(
				a -> a.field("김치").value("볶음밥","라면")
			)
		);
		System.out.println(filter);

	}

}
