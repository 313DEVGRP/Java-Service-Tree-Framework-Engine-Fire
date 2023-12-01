package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

public class RangeQueryFilter extends Range {

    private RangeQueryBuilder rangeQueryBuilder;

    public RangeQueryFilter(String name, Object from, Object to, String flag) {
        if (name != null && flag != null) {
            if("lt".equals(flag)) {
                if(to != null) {
                    this.rangeQueryBuilder = QueryBuilders.rangeQuery(name).lt(to);
                }
            } else if ("lte".equals(flag)) {
                if(to != null) {
                    this.rangeQueryBuilder = QueryBuilders.rangeQuery(name).lte(to);
                }
            } else if ("gt".equals(flag)) {
                if(from != null) {
                    this.rangeQueryBuilder = QueryBuilders.rangeQuery(name).gt(from);
                }
            } else if ("gte".equals(flag)) {
                if(from != null) {
                    this.rangeQueryBuilder = QueryBuilders.rangeQuery(name).gte(from);
                }
            } else if ("fromto".equals(flag)) {
                if(from != null && to != null) {
                    this.rangeQueryBuilder = QueryBuilders.rangeQuery(name).from(from).to(to);
                }
            }
        }
    }

    public RangeQueryBuilder rangeQueryBuilder() {
        return rangeQueryBuilder;
    }
}
