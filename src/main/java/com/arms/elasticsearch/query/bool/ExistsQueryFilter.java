package com.arms.elasticsearch.query.bool;

import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class ExistsQueryFilter extends Filter<ExistsQueryBuilder> {

    private ExistsQueryBuilder existsQueryBuilder;

    public ExistsQueryFilter(String name) {
        if (name != null) {
            this.existsQueryBuilder = QueryBuilders.existsQuery(name);
        }
    }

    @Override
    public AbstractQueryBuilder<ExistsQueryBuilder> abstractQueryBuilder() {
        return existsQueryBuilder;
    }
}
