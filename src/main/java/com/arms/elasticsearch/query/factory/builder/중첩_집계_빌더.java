package com.arms.elasticsearch.query.factory.builder;

import com.arms.elasticsearch.query.base.집계_하위_요청;

import java.util.List;

public interface 중첩_집계_빌더<T> {
    T createAggregation(List<집계_하위_요청> 하위그룹필드, int size);
}
