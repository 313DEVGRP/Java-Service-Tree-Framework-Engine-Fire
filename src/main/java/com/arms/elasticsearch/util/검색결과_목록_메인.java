package com.arms.elasticsearch.util;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.filter.ParsedFilter;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.core.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.SearchHits;

import lombok.Getter;

@Getter
public class 검색결과_목록_메인 {

	private Long 전체합계;
	private Map<String,List<검색결과>> 검색결과;

	public List<검색결과> 그룹결과(String name){
		return 검색결과.get(name);
	}

	public 검색결과_목록_메인(SearchHits searchHits) {
		this.전체합계 = searchHits.getTotalHits();
		this.검색결과 = new 검색결과_목록_메인(getTerm(searchHits.getAggregations())
			.entrySet()
			.stream()
			.collect(toMap(aggregation -> aggregation.getKey(), a -> a.getValue()))).get검색결과();
	}

	public 검색결과_목록_메인(Map<String, Aggregation> bucketsAggregationMap) {

		this.검색결과 = bucketsAggregationMap.entrySet().stream()
			.collect(groupingBy(a->new 그룹이름(a.getKey()).get()
				,flatMapping(buckets-> 버킷종류별_분기(buckets)
							,toList())));
	}

	private Stream<검색결과> 버킷종류별_분기(Map.Entry<String, Aggregation> buckets) {

		if( buckets.getValue() instanceof ParsedFilter){
			 return Stream.of((ParsedFilter) buckets.getValue())
				.map(bucket ->  new 검색결과(bucket));
		}

		return ((MultiBucketsAggregation) buckets.getValue())
				.getBuckets()
				.stream().map(bucket -> new 검색결과(bucket));
	}


	private Map<String, Aggregation> getTerm(AggregationsContainer aggregationsContainer){
		Aggregations aggregations = getAggregations(aggregationsContainer);
		return aggregations.getAsMap();
	}

	private Aggregations getAggregations(AggregationsContainer aggregationsContainer){
		return ((ElasticsearchAggregations) aggregationsContainer).aggregations();
	}


}
