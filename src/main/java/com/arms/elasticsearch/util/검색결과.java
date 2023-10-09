package com.arms.elasticsearch.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(exclude = "count")
@Getter
public class 검색결과 {

    private String 필드명;
    private long 개수;
    private 검색결과_목록 검색결과_목록;

    public 검색결과(String 필드명,long 개수,Terms.Bucket subBucket) {
        this.필드명 = 필드명;
        this.개수 = 개수;
        this.검색결과_목록 = new 검색결과_목록(subBucket.getAggregations()
            .asList()
            .stream()
            .flatMap(a -> ((ParsedStringTerms)a).getBuckets()
                .stream()
                .map(b -> new 검색결과(b.getKeyAsString(), b.getDocCount())))
            .collect(Collectors.toList()));
    }

    public 검색결과(String 필드명,long 개수) {
        this.필드명 = 필드명;
        this.개수 = 개수;
    }


}
