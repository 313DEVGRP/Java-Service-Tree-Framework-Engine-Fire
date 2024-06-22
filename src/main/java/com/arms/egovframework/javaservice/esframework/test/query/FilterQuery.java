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
            f->f.filter(
                t->t.term(
                    fd->fd.field("host_link")
                           .value("59L")
                           .and()
                           .field("ip")
                           .value("172.31.3.131")
                           .build()
                )
            )
        );
    }

}
