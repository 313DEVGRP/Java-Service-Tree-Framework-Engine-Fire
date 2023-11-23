package com.arms.elasticsearch.util.aggregation;

import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;

import java.util.Map;

public abstract class CustomAbstractAggregationBuilder<T extends AbstractAggregationBuilder<T>> {
    protected T aggregationBuilder;
    private Map<String, Object> metadata;

    public CustomAbstractAggregationBuilder(T aggregationBuilder) {
        this.aggregationBuilder = aggregationBuilder;
    }

    public CustomAbstractAggregationBuilder<T> addSubAggregation(AggregationBuilder subAggregation) {
        this.aggregationBuilder.subAggregation(subAggregation);
        return this;
    }

    public CustomAbstractAggregationBuilder<T> addPipelineAggregation(PipelineAggregationBuilder pipelineAggregation) {
        this.aggregationBuilder.subAggregation(pipelineAggregation);
        return this;
    }

    public CustomAbstractAggregationBuilder<T> setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    public T build() {
        return this.aggregationBuilder;
    }
}