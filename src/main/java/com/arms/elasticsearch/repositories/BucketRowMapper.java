package com.arms.elasticsearch.repositories;

import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

public interface BucketRowMapper<T> {

	T bucketRow(MultiBucketsAggregation.Bucket bucket);
}
