package com.arms.elasticsearch.query.factory.builder;

import com.arms.elasticsearch.query.base.하위_집계;

import java.util.List;

public interface 하위_집계_빌더<T> {
    T createAggregation(List<하위_집계> 하위그룹필드, int size);
}
