package com.arms.elasticsearch.util.query;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.List;

public class 서브_집계_요청 {

    private List<String> 하위그룹필드;
    private int size;

    public 서브_집계_요청(List<String> 하위그룹필드, int size){
        this.하위그룹필드 = 하위그룹필드;
    }

    public AggregationBuilder createNestedAggregation(List<String> 하위_그룹필드들, int size) {
        return
                하위_그룹필드들
                        .stream()
                        .map(groupField ->
                                AggregationBuilders.terms("group_by_" + groupField)
                                        .field(groupField)
                                        .size(size))
                        .reduce(null, (agg1, agg2) -> {
                            if (agg1 == null) {
                                return agg2;
                            } else {
                                return agg1.subAggregation(agg2);
                            }
                        });
    }
}
