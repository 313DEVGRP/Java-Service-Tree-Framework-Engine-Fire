package com.arms.egovframework.javaservice.esframework.filter;

import com.arms.egovframework.javaservice.esframework.Filter;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

import java.util.List;

public class QueryStringFilter extends Filter<QueryStringQueryBuilder> {

    private QueryStringQueryBuilder queryBuilder;

    public QueryStringFilter(String queryString){
        if(queryString!=null){
            this.queryBuilder = QueryBuilders.queryStringQuery(queryString);
        }
    }

    public QueryStringFilter(String queryString, List<String> fields){
        if(queryString!=null){
            this.queryBuilder = QueryBuilders.queryStringQuery(queryString);

        }
    }

    @Override
    public AbstractQueryBuilder<QueryStringQueryBuilder> abstractQueryBuilder() {
        return queryBuilder;
    }
}
