package com.arms.elasticsearch;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.aggregations.metrics.ParsedSingleValueNumericMetricsAggregation;

@EqualsAndHashCode(exclude = "개수")
@Getter
@NoArgsConstructor
public class 메트릭_집계_결과 {

    private String 필드명;
    private double 개수;

    public 메트릭_집계_결과(ParsedSingleValueNumericMetricsAggregation metricsAggregation) {
        this.필드명 = metricsAggregation.getName();
        this.개수 = metricsAggregation.value();
    }



}
