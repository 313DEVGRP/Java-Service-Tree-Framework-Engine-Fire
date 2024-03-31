package com.arms.elasticsearch;

import lombok.Getter;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.metrics.ParsedSingleValueNumericMetricsAggregation;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Getter
public class 메트릭_집계_결과_목록_합계 {

	private Long 전체합계;
	private Map<String,List<메트릭_집계_결과>> 검색결과;

	public List<메트릭_집계_결과> 그룹결과(String name){
		return 검색결과.get(name);
	}

	public 메트릭_집계_결과_목록_합계(SearchHits searchHits) {
		this.전체합계 = searchHits.getTotalHits();
		this.검색결과 = new 메트릭_집계_결과_목록_합계(getAsMap(searchHits.getAggregations())
			.entrySet()
			.stream()
			.collect(toMap(Entry::getKey, Entry::getValue))).get검색결과();
	}

	public 메트릭_집계_결과_목록_합계(Map<String, Aggregation> 메트릭_집계_맵) {
		this.검색결과 = 메트릭_집계_맵.entrySet().stream()
				.collect(groupingBy(a->new 집계_결과_그룹(a.getKey()).get()
						,flatMapping(this::매트릭_집계_결과_매핑,toList())));
	}

	private Stream<메트릭_집계_결과> 매트릭_집계_결과_매핑 (Entry<String, Aggregation> 메트릭_집계_맵) {
		 return Stream.of((ParsedSingleValueNumericMetricsAggregation) 메트릭_집계_맵.getValue())
			.map(메트릭_집계_결과::new);
	}

	private Map<String, Aggregation> getAsMap(Aggregations aggregationsContainer){
		return Optional.ofNullable(aggregationsContainer)
					.map(Aggregations::getAsMap)
					.orElseGet(HashMap::new);
	}

}
