package com.arms.elasticsearch.query.factory.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.일반_집계_요청;
import com.arms.elasticsearch.query.base.하위_집계_요청;
import com.arms.elasticsearch.query.builder.계층_하위_집계_빌더;
import com.arms.elasticsearch.query.builder.비계층_하위_집계_빌더;
import com.arms.elasticsearch.query.builder.하위_집계_빌더;

import lombok.Getter;

@Getter
public class 일자별_집계_쿼리 implements 집계_쿼리 {

    private List<String> _하위_그룹_필드들 = new ArrayList<>();
    private final String 메인그룹필드;
    private final int 크기;
    private final int 하위크기;
    private final boolean 컨텐츠보기여부;
    private final NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

    private final DateHistogramAggregationBuilder dateHistogramAggregationBuilder;
    private final BoolQueryBuilder boolQuery;

    private 일자별_집계_쿼리(일반_집계_요청 일반_집계_요청, EsQuery esQuery,DateHistogramInterval dateHistogramInterval){

        if(일반_집계_요청 instanceof 하위_집계_요청){
            this._하위_그룹_필드들 = ((하위_집계_요청)일반_집계_요청).get하위그룹필드들();
        }

        this.메인그룹필드 = 일반_집계_요청.get메인그룹필드();
        this.크기 = 일반_집계_요청.get크기();
        this.컨텐츠보기여부 = 일반_집계_요청.is컨텐츠보기여부();
        this.dateHistogramAggregationBuilder = new DateHistogramAggregationBuilder(
            "date_group_by_" + 일반_집계_요청.get메인그룹필드())
            .field(일반_집계_요청.get메인그룹필드())
            .calendarInterval(dateHistogramInterval)
            .minDocCount(0); // 집계 간격을 지정

        this.하위크기 = 일반_집계_요청.get하위크기();
        this.boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});
        this.nativeSearchQueryBuilder.withMaxResults(컨텐츠보기여부 ? 크기 : 0);

        this.nativeSearchQueryBuilder.addAggregation(
                this.dateHistogramAggregationBuilder
        );

        Optional.ofNullable(boolQuery)
            .ifPresent(query->{
                this.nativeSearchQueryBuilder.withQuery(boolQuery);
            });
    }

    public static 일자별_집계_쿼리 week(EsQuery esQuery){
        return new 일자별_집계_쿼리(new 일반_집계_요청() {},esQuery,DateHistogramInterval.WEEK);
    }

    public static 일자별_집계_쿼리 week(일반_집계_요청 일반_집계_요청, EsQuery esQuery){
        return new 일자별_집계_쿼리(일반_집계_요청,esQuery,DateHistogramInterval.WEEK);
    }

    public static 일자별_집계_쿼리 week(하위_집계_요청 하위_집계_요청, EsQuery esQuery){
        return new 일자별_집계_쿼리(하위_집계_요청,esQuery,DateHistogramInterval.WEEK);
    }

    public static 일자별_집계_쿼리 day(EsQuery esQuery){
        return new 일자별_집계_쿼리(new 일반_집계_요청() {},esQuery,DateHistogramInterval.DAY);
    }

    public static 일자별_집계_쿼리 day(일반_집계_요청 일반_집계_요청, EsQuery esQuery){
        return new 일자별_집계_쿼리(일반_집계_요청,esQuery,DateHistogramInterval.DAY);
    }

    public static 일자별_집계_쿼리 day(하위_집계_요청 하위_집계_요청, EsQuery esQuery){
        return new 일자별_집계_쿼리(하위_집계_요청,esQuery,DateHistogramInterval.DAY);
    }

    @Override
    public void 계층_하위_집계_빌더_적용(){
        서브_집계_기본틀(new 계층_하위_집계_빌더());
    }

    @Override
    public void 형제_하위_집계_빌더_적용(){
        서브_집계_기본틀(new 비계층_하위_집계_빌더());
    }

    private void 서브_집계_기본틀(하위_집계_빌더 하위_집계_빌더){
        Function<하위_집계_빌더, AggregationBuilder> function
            = a -> a.createAggregation(_하위_그룹_필드들,하위크기);
        Optional.ofNullable(_하위_그룹_필드들)
            .ifPresent(__하위_그룹_필드들->{
                if(!__하위_그룹_필드들.isEmpty()){
                    dateHistogramAggregationBuilder
                        .subAggregation(
                            function.apply(하위_집계_빌더)
                        );
                }
            });
    }

    @Override
    public NativeSearchQuery 생성(){
        return nativeSearchQueryBuilder.build();
    }
}
