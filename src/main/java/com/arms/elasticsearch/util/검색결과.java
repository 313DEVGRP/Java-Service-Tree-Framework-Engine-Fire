package com.arms.elasticsearch.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(exclude = "count")
@Getter
public class 검색결과 {

    private String value;
    private long count;

    public 검색결과(String value, long count) {
        this.value = value;
        this.count = count;
    }

}
