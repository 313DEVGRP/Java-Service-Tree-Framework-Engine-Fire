package com.arms.egovframework.javaservice.esframework.factory.builder;

import com.arms.egovframework.javaservice.esframework.model.dto.집계_하위_요청;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class 중첩_집계_메트릭_빌더 {

    public List<AggregationBuilder> createAggregation(List<집계_하위_요청> 하위그룹필드, int size) {
          return 하위그룹필드.stream()
                .flatMap(하위필드명 -> Stream.of(
                      AggregationBuilders.avg("avg_by_" + 하위필드명.get하위_필드명()).field(하위필드명.get하위_필드명())
                    , AggregationBuilders.max("max_by_" + 하위필드명.get하위_필드명()).field(하위필드명.get하위_필드명())
                    , AggregationBuilders.min("min_by_" + 하위필드명.get하위_필드명()).field(하위필드명.get하위_필드명()))
                ).collect(Collectors.toList());
    }

}
