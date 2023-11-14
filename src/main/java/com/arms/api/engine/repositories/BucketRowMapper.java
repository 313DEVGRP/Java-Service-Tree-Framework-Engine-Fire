package com.arms.api.engine.repositories;

import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;

public interface BucketRowMapper<T> {

	T bucketRow(MultiBucketsAggregation.Bucket bucket);
}
