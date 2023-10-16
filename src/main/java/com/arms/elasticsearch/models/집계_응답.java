package com.arms.elasticsearch.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class 집계_응답 {
    private String key;
    private long docCount;
}
