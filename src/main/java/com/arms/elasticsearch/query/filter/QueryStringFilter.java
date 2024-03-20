package com.arms.elasticsearch.query.filter;

import com.arms.elasticsearch.query.Filter;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

public class QueryStringFilter extends Filter<QueryStringQueryBuilder> {

    private QueryStringQueryBuilder queryBuilder;

    public QueryStringFilter(String queryString){
        if(queryString!=null){
            this.queryBuilder = QueryBuilders.queryStringQuery(queryString);
        }
    }

    @Override
    public AbstractQueryBuilder<QueryStringQueryBuilder> abstractQueryBuilder() {
        return queryBuilder;
    }
}
