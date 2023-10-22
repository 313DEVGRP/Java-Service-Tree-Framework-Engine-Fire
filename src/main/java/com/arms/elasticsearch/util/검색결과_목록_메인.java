package com.arms.elasticsearch.util;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;

import lombok.Getter;

@Getter
public class 검색결과_목록_메인 {

	private final Map<String,List<검색결과>> 검색결과;

	public 검색결과_목록_메인(Map<String, Aggregation> bucketsAggregationMap) {
		this.검색결과 = bucketsAggregationMap.entrySet().stream()
			.collect(groupingBy(a->new 그룹이름(a.getKey()).get()
				,flatMapping(buckets->((MultiBucketsAggregation)buckets.getValue()).getBuckets()
					.stream().map(bucket -> new 검색결과(bucket)),toList())));
	}

}
