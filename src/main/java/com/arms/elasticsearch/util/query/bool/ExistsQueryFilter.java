package com.arms.elasticsearch.util.query.bool;

import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class ExistsQueryFilter extends Exist {

    private ExistsQueryBuilder existsQueryBuilder;

    public ExistsQueryFilter(String name) {
        if (name != null) {
            this.existsQueryBuilder = QueryBuilders.existsQuery(name);
        }
    }

    public ExistsQueryBuilder existsQueryBuilder() {
        return existsQueryBuilder;
    }
}
