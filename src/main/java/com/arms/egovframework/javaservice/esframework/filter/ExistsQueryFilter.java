package com.arms.egovframework.javaservice.esframework.filter;

import com.arms.egovframework.javaservice.esframework.Filter;
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
