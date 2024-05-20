package com.arms.elasticsearch.query.factory.builder;

import com.arms.elasticsearch.query.base.집계_하위_요청;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.List;
import java.util.Optional;

import static org.elasticsearch.search.aggregations.BucketOrder.*;

public class 중첩_집계_포괄_빌더 implements 중첩_집계_빌더<AggregationBuilder> {

    @Override
    public AggregationBuilder createAggregation(List<집계_하위_요청> 하위그룹필드, int size) {
        return
            하위그룹필드
                    .stream()
                    .map(하위필드명 ->
                            AggregationBuilders.terms(Optional.ofNullable(하위필드명.get하위_필드명_별칭()).orElseGet(()->"group_by_"+하위필드명.get하위_필드명()))
                                .field(하위필드명.get하위_필드명())
                                .order(count(하위필드명.is결과_갯수_기준_오름차순()))
                                .size(size)
                    )
                    .reduce(null, (agg1, agg2) -> {
                        if (agg1 == null) {
                            return agg2;
                        } else {
                            return agg1.subAggregation(agg2);
                        }
                    });
    }

}
