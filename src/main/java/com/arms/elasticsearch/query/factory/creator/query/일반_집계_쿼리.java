package com.arms.elasticsearch.query.factory.creator.query;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.기본_검색_집계_요청;
import com.arms.elasticsearch.query.base.기본_검색_집계_하위_요청;
import lombok.Getter;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

@Getter
public class 일반_집계_쿼리 extends 공통_집계_쿼리<TermsAggregationBuilder>{

    private 일반_집계_쿼리(기본_검색_집계_요청 _일반_집계_요청, EsQuery esQuery){
        super(
            _일반_집계_요청
            ,esQuery
            ,AggregationBuilders.terms("group_by_" + _일반_집계_요청.get메인그룹필드())
                .field(_일반_집계_요청.get메인그룹필드())
                .order(BucketOrder.count(_일반_집계_요청.is결과_갯수_기준_오름차순()))
                .size(_일반_집계_요청.get크기())
        );
    }

    public static 일반_집계_쿼리 of(EsQuery esQuery){
        return new 일반_집계_쿼리(new 기본_검색_집계_요청() {},esQuery);
    }

    public static 일반_집계_쿼리 of(기본_검색_집계_요청 일반_집계_요청, EsQuery esQuery){
        return new 일반_집계_쿼리(일반_집계_요청,esQuery);
    }

    public static 일반_집계_쿼리 of(기본_검색_집계_하위_요청 하위_집계_요청, EsQuery esQuery){
        return new 일반_집계_쿼리(하위_집계_요청,esQuery);
    }

}
