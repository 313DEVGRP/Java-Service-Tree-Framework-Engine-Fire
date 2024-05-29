package com.arms.elasticsearch.query.factory.creator.query;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.model.dto.기본_검색_집계_요청;
import com.arms.elasticsearch.query.model.dto.기본_검색_집계_하위_요청;
import lombok.Getter;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

@Getter
public class 기본_집계_쿼리 extends 공통_집계_쿼리<TermsAggregationBuilder>{

    private 기본_집계_쿼리(기본_검색_집계_요청 기본_검색_집계_요청, EsQuery esQuery){
        super(
                기본_검색_집계_요청
            ,esQuery
            ,AggregationBuilders.terms("group_by_" + 기본_검색_집계_요청.get메인그룹필드())
                .field(기본_검색_집계_요청.get메인그룹필드())
                .order(BucketOrder.count(기본_검색_집계_요청.is결과_갯수_기준_오름차순()))
                .size(기본_검색_집계_요청.get크기())
        );
    }
    public static 기본_집계_쿼리 of(EsQuery esQuery){
        return new 기본_집계_쿼리(new 기본_검색_집계_요청() {},esQuery);
    }

    public static 기본_집계_쿼리 of(기본_검색_집계_요청 기본_검색_집계_요청, EsQuery esQuery){
        return new 기본_집계_쿼리(기본_검색_집계_요청,esQuery);
    }

    public static 기본_집계_쿼리 of(기본_검색_집계_하위_요청 기본_검색_집계_하위_요청, EsQuery esQuery){
        return new 기본_집계_쿼리(기본_검색_집계_하위_요청,esQuery);
    }

}
