package com.arms.egovframework.javaservice.esframework.test.query;

import com.arms.egovframework.javaservice.esframework.test.Query;
import com.arms.egovframework.javaservice.esframework.test.builder.FilterBuilder;

import java.util.function.Consumer;

public class FilterQuery {

    public static Query query(Consumer<FilterBuilder> filterBuilder){
        Query query = new Query();
        return query;
    }

    public static void main(String[] args) {
        query(
            b->b.filter(
                c->c.field("host_link").value("73")
                    .and()
                    .field("recent").value("true")
            )
        );
    }

}
