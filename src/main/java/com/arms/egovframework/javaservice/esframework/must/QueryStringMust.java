package com.arms.egovframework.javaservice.esframework.must;

import com.arms.egovframework.javaservice.esframework.Must;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

public class QueryStringMust extends Must<QueryStringQueryBuilder> {

    private QueryStringQueryBuilder queryBuilder;

    public QueryStringMust(String queryString){
        if(queryString!=null){
            this.queryBuilder = QueryBuilders.queryStringQuery(queryString);
        }
    }

    @Override
    public AbstractQueryBuilder<QueryStringQueryBuilder> abstractQueryBuilder() {
        return queryBuilder;
    }
}
