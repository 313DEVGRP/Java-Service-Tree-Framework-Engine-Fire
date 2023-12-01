package com.arms.elasticsearch.util.aggregation;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

public class CustomDateHistogramAggregationBuilder extends CustomAbstractAggregationBuilder<DateHistogramAggregationBuilder> {

    public CustomDateHistogramAggregationBuilder(String name) {
        super(AggregationBuilders.dateHistogram(name));
    }

    public CustomDateHistogramAggregationBuilder field(String field) {
        this.aggregationBuilder.field(field);
        return this;
    }

    public CustomDateHistogramAggregationBuilder calendarInterval(DateHistogramInterval interval) {
        this.aggregationBuilder.calendarInterval(interval);
        return this;
    }
}