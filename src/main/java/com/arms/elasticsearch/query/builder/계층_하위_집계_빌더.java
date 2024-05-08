package com.arms.elasticsearch.query.builder;

import com.arms.elasticsearch.query.base.하위_집계;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.List;
import java.util.Optional;

public class 계층_하위_집계_빌더 implements 하위_집계_빌더<AggregationBuilder>{

    @Override
    public AggregationBuilder createAggregation(List<하위_집계> 하위그룹필드, int size) {
        return
            하위그룹필드
                    .stream()
                    .map(하위필드명 ->
                            AggregationBuilders.terms(Optional.ofNullable(하위필드명.get별칭()).orElseGet(()->"group_by_"+하위필드명.get필드명()))
                                .field(하위필드명.get필드명())
                                .size(size))
                    .reduce(null, (agg1, agg2) -> {
                        if (agg1 == null) {
                            return agg2;
                        } else {
                            return agg1.subAggregation(agg2);
                        }
                    });
    }

    public static void main(String[] args) {
        boolean b = true;

        String s = Optional.of(b).filter(x -> x).map(x -> "트").orElseGet(() -> "크");
        System.out.println(s);
    }


}
