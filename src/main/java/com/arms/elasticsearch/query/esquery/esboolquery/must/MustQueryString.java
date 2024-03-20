package com.arms.elasticsearch.query.esquery.esboolquery.must;

import com.arms.elasticsearch.query.esquery.esboolquery.Must;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

public class MustQueryString extends Must<QueryStringQueryBuilder> {

    private QueryStringQueryBuilder queryBuilder;

    public MustQueryString(String queryString){
        if(queryString!=null){
            this.queryBuilder = QueryBuilders.queryStringQuery(queryString);
        }
    }

    @Override
    public AbstractQueryBuilder<QueryStringQueryBuilder> abstractQueryBuilder() {
        return queryBuilder;
    }
}
