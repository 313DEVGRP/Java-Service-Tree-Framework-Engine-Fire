package com.arms.elasticsearch.query.factory.creator.query;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.model.dto.기본_검색_집계_요청;
import com.arms.elasticsearch.query.model.dto.기본_검색_집계_하위_요청;
import lombok.Getter;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;

@Getter
public class 일자별_집계_쿼리 extends 공통_집계_쿼리<DateHistogramAggregationBuilder> {

    private 일자별_집계_쿼리(기본_검색_집계_요청 기본_검색_집계_요청, EsQuery esQuery, DateHistogramInterval dateHistogramInterval){
        super(
             기본_검색_집계_요청
            ,esQuery
            ,new DateHistogramAggregationBuilder("date_group_by_" + 기본_검색_집계_요청.get메인그룹필드())
                .field(기본_검색_집계_요청.get메인그룹필드())
                .calendarInterval(dateHistogramInterval)
                .minDocCount(0)
        );
    }

    public static 일자별_집계_쿼리 week(EsQuery esQuery){
        return new 일자별_집계_쿼리(new 기본_검색_집계_요청() {},esQuery,DateHistogramInterval.WEEK);
    }

    public static 일자별_집계_쿼리 week(기본_검색_집계_요청 기본_검색_집계_요청, EsQuery esQuery){
        return new 일자별_집계_쿼리(기본_검색_집계_요청,esQuery,DateHistogramInterval.WEEK);
    }

    public static 일자별_집계_쿼리 week(기본_검색_집계_하위_요청 기본_검색_집계_하위_요청, EsQuery esQuery){
        return new 일자별_집계_쿼리(기본_검색_집계_하위_요청,esQuery,DateHistogramInterval.WEEK);
    }

    public static 일자별_집계_쿼리 day(EsQuery esQuery){
        return new 일자별_집계_쿼리(new 기본_검색_집계_요청() {},esQuery,DateHistogramInterval.DAY);
    }

    public static 일자별_집계_쿼리 day(기본_검색_집계_요청 기본_검색_집계_요청, EsQuery esQuery){
        return new 일자별_집계_쿼리(기본_검색_집계_요청,esQuery,DateHistogramInterval.DAY);
    }

    public static 일자별_집계_쿼리 day(기본_검색_집계_하위_요청 기본_검색_집계_하위_요청, EsQuery esQuery){
        return new 일자별_집계_쿼리(기본_검색_집계_하위_요청,esQuery,DateHistogramInterval.DAY);
    }

}
