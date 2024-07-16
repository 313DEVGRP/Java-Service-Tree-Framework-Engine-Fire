package com.arms.egovframework.javaservice.esframework.test;

import java.util.Arrays;
import java.util.function.Consumer;

import lombok.Getter;

@Getter
public class FilterBuilder {

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

	private static TermsBuilder buildTerms(Consumer<TermsBuilder> consumer) {
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


}
