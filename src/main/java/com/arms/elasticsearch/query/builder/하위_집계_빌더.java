package com.arms.elasticsearch.query.builder;

import org.elasticsearch.search.aggregations.AggregationBuilder;

import java.util.List;

public interface 하위_집계_빌더 {
    AggregationBuilder createAggregation(List<String> 하위그룹필드, int size);
}
