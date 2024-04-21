package com.arms.elasticsearch.query.builder;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.List;

public class 계층_하위_집계_빌더 implements 하위_집계_빌더{

    @Override
    public AggregationBuilder createAggregation(List<String> 하위그룹필드, int size) {
        return
            하위그룹필드
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
