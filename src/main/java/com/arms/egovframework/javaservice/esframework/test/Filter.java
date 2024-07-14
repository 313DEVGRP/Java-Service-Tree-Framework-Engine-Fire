package com.arms.egovframework.javaservice.esframework.test;

import java.util.ArrayList;
import java.util.List;

public class Filter {

	List<Term> termList = new ArrayList<>();

	List<Terms> termsList = new ArrayList<>();

	public void addFilter(Term term){
		termList.add(term);
	}

	public void addFilter(Terms terms){
		termsList.add(terms);
	}
}
