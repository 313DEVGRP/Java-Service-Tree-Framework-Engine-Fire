package com.arms.elasticsearch.repositories;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;


public class BucketConvertor<T> {

	private final List<T> list;

	public BucketConvertor(List<T> list){
		this.list = list;
	}


}
