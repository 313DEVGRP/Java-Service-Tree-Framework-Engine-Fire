package com.arms.elasticsearch.repositories;

import org.elasticsearch.search.aggregations.bucket.terms.Terms;

public interface BucketRowMapper<T> {

	T bucketRow(Terms.Bucket bucket);
}
