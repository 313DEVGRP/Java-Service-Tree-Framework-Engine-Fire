package com.arms.elasticsearch.util.query.bool;

import java.time.LocalDate;

import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

public class RangeQueryFilter extends Filter<RangeQueryBuilder> {

    private RangeQueryBuilder rangeQueryBuilder;

    private RangeQueryFilter(){

    }

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

    private RangeQueryFilter(String name){
        this.rangeQueryBuilder = QueryBuilders.rangeQuery(name);
    }

    public static RangeQueryFilter of(String name){
        return new RangeQueryFilter(name);
    }

    public RangeQueryFilter lt(LocalDate to){
         this.rangeQueryBuilder.lt(to);
         return this;
    }

    public RangeQueryFilter lte(LocalDate to){
        this.rangeQueryBuilder.lte(to);
        return this;
    }

    public RangeQueryFilter gt(LocalDate from){
        this.rangeQueryBuilder.gt(from);
        return this;
    }

    public RangeQueryFilter gte(LocalDate from){
        this.rangeQueryBuilder.gte(from);
        return this;
    }

    public RangeQueryFilter from(LocalDate from){
        this.rangeQueryBuilder.from(from);
        return this;
    }

    public RangeQueryFilter to(LocalDate to){
        this.rangeQueryBuilder.to(to);
        return this;
    }

    @Override
    public AbstractQueryBuilder<RangeQueryBuilder>abstractQueryBuilder() {
        return rangeQueryBuilder;
    }
}
