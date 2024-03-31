package com.arms.elasticsearch;

import static java.util.stream.Collectors.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.springframework.data.elasticsearch.core.SearchHits;

import lombok.Getter;

@Getter
public class 버킷_집계_결과_목록_합계 {

	private Long 전체합계;
	private Map<String,List<버킷_집계_결과>> 검색결과;

	public List<버킷_집계_결과> 그룹결과(String name){
		return 검색결과.get(name);
	}

	public 버킷_집계_결과_목록_합계(SearchHits searchHits) {
		this.전체합계 = searchHits.getTotalHits();
		this.검색결과 = new 버킷_집계_결과_목록_합계(getTerm(searchHits.getAggregations())
			.entrySet()
			.stream()
			.collect(toMap(aggregation -> aggregation.getKey(), a -> a.getValue()))).get검색결과();
	}

	public 버킷_집계_결과_목록_합계(Map<String, Aggregation> bucketsAggregationMap) {

		this.검색결과 = bucketsAggregationMap.entrySet().stream()
			.collect(groupingBy(a->new 집계_결과_그룹(a.getKey()).get()
				,flatMapping(buckets-> 버킷종류별_분기(buckets)
							,toList())));
	}

	private Stream<버킷_집계_결과> 버킷종류별_분기(Map.Entry<String, Aggregation> buckets) {

		if( buckets.getValue() instanceof ParsedFilter){
			 return Stream.of((ParsedFilter) buckets.getValue())
				.map(bucket ->  new 버킷_집계_결과(bucket));
		}

		return ((MultiBucketsAggregation) buckets.getValue())
				.getBuckets()
				.stream().map(bucket -> new 버킷_집계_결과(bucket));
	}


	private Map<String, Aggregation> getTerm(Aggregations aggregationsContainer){
		return Optional.ofNullable(aggregationsContainer).map(Aggregations::getAsMap).orElseGet(
			HashMap::new);
	}



}
