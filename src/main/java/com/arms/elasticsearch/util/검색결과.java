package com.arms.elasticsearch.util;

import java.util.List;

import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(exclude = "count")
@Getter
public class 검색결과 {

    private String 필드명;
    private long 개수;
    private Terms.Bucket subBucket;

    public 검색결과(String 필드명,long 개수,Terms.Bucket subBucket) {
        this.필드명 = 필드명;
        this.개수 = 개수;
        this.subBucket = subBucket;
    }

    public 검색결과(String 필드명,long 개수) {
        this.필드명 = 필드명;
        this.개수 = 개수;
    }


}
