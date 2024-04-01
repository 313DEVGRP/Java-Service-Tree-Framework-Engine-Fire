package com.arms.elasticsearch;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation.Bucket;
import org.elasticsearch.search.aggregations.metrics.ParsedSingleValueNumericMetricsAggregation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EqualsAndHashCode(exclude = "개수")
@Getter
@NoArgsConstructor
public class 메트릭_집계_결과 {

    private String 필드명;
    private double 개수;
    private Map<String, List<메트릭_집계_결과>> 하위검색결과;

    public 메트릭_집계_결과(Bucket bucket) {
        this.필드명 = bucket.getKeyAsString();
        this.개수 = bucket.getDocCount();
        this.하위검색결과 = new 메트릭_집계_결과_목록_합계(bucket.getAggregations().asMap()).get검색결과();
    }

    public 메트릭_집계_결과(ParsedSingleValueNumericMetricsAggregation parsedValue) {
        this.필드명 = parsedValue.getValueAsString();
        this.개수 = parsedValue.value();
    }

    public List<메트릭_집계_결과> 하위검색결과(String name){
        return 하위검색결과.get(name);
    }

    public Double 필터필드개수(String name){
        return 하위검색결과.get(name).stream().findFirst().map(a->a.get개수()).orElse(0D);
    }

    public String 필터필드명(String name){
        return 하위검색결과.get(name).stream().findFirst().map(a->a.get필드명()).orElse("N/A");
    }

    public Map<String,Double> 검색결과_맵처리(String name){
        return 하위검색결과.get(name).stream()
                .collect(Collectors.toMap(검색결과->검색결과.get필드명(),검색결과->검색결과.get개수(),(p1,p2)->p1));
    }



}
