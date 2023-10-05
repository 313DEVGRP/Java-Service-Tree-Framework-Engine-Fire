package com.arms.elasticsearch.util;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(exclude = "count")
public class 검색결과 {

    private String value;
    private long count;

    public 검색결과(String value, long count) {
        this.value = value;
        this.count = count;
    }

}
