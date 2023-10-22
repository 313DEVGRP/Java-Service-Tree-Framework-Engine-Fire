package com.arms.elasticsearch.util;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(exclude = "count")
@Getter
public class 검색결과 {

    private String 필드명;
    private long 개수;
    private Map<String, List<검색결과>> 하위검색결과;

    public 검색결과(String 필드명,long 개수) {
        this.필드명 = 필드명;
        this.개수 = 개수;
    }

    public 검색결과(Bucket bucket) {
        this.필드명 = bucket.getKeyAsString();
        this.개수 = bucket.getDocCount();
        this.하위검색결과 = new 검색결과_목록_메인(bucket.getAggregations().asMap()).get검색결과();
    }



}
