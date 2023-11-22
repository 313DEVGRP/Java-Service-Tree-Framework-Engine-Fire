package com.arms.elasticsearch.util.aggregation;

import org.elasticsearch.script.Script;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

public class CustomTermsAggregationBuilder extends CustomAbstractAggregationBuilder<TermsAggregationBuilder> {

    public CustomTermsAggregationBuilder(String name) {
        super(AggregationBuilders.terms(name));
    }


    public CustomTermsAggregationBuilder field(String field) {
        this.aggregationBuilder.field(field);
        return this;
    }

    public CustomTermsAggregationBuilder order(BucketOrder order) {
        this.aggregationBuilder.order(order);
        return this;
    }

    public CustomTermsAggregationBuilder size(int size) {
        this.aggregationBuilder.size(size > 0 ? size : Integer.MAX_VALUE);
        return this;
    }

    public CustomTermsAggregationBuilder minDocCount(int minDocCount) {
        this.aggregationBuilder.minDocCount(minDocCount);
        return this;
    }

    public CustomTermsAggregationBuilder missing(Object value) {
        this.aggregationBuilder.missing(value);
        return this;
    }

    public CustomTermsAggregationBuilder script(Script script) {
        this.aggregationBuilder.script(script);
        return this;
    }
}