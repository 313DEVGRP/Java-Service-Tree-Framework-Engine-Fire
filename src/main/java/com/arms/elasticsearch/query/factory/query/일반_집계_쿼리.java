package com.arms.elasticsearch.query.factory.query;

import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.base.기본_집계_요청;
import com.arms.elasticsearch.query.builder.계층_하위_집계_빌더;
import com.arms.elasticsearch.query.builder.하위_집계_빌더;
import com.arms.elasticsearch.query.builder.비계층_하위_집계_빌더;
import lombok.Getter;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Getter
public class 일반_집계_쿼리 {

    private final List<String> 하위그룹필드들;
    private final String 메인그룹필드;
    private final int 크기;
    private final int 하위크기;
    private final boolean 컨텐츠보기여부;
    private final NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
    private final BoolQueryBuilder boolQuery;
    private final TermsAggregationBuilder termsAggregationBuilder;
    private final List<FieldSortBuilder> fieldSortBuilders;

    public 일반_집계_쿼리(기본_집계_요청 기본_집계_요청, EsQuery esQuery){
        this.하위그룹필드들 = 기본_집계_요청.get하위그룹필드들();
        this.메인그룹필드 = 기본_집계_요청.get메인그룹필드();
        this.크기 = 기본_집계_요청.get크기();
        this.컨텐츠보기여부 = 기본_집계_요청.is컨텐츠보기여부();
        this.하위크기 = 기본_집계_요청.get하위크기();
        this.boolQuery = esQuery.getQuery(new ParameterizedTypeReference<>() {});
        this.nativeSearchQueryBuilder.withMaxResults(컨텐츠보기여부 ? 크기 : 0);

        this.termsAggregationBuilder = AggregationBuilders.terms("group_by_" + 메인그룹필드)
                .field(메인그룹필드)
                .order(BucketOrder.count(기본_집계_요청.is결과_갯수_기준_오름차순()))
                .size(크기);

        this.nativeSearchQueryBuilder.addAggregation(
                this.termsAggregationBuilder
        );

        this.fieldSortBuilders =  esQuery.getQuery(new ParameterizedTypeReference<>(){});

        Optional.ofNullable(boolQuery)
                .ifPresent(query->{
                    this.nativeSearchQueryBuilder.withQuery(boolQuery);
                });

        Optional.ofNullable(fieldSortBuilders)
                .ifPresent(sorts -> {
                    sorts.forEach(nativeSearchQueryBuilder::withSort);
                });
    }

    public void 계층_하위_집계_빌더_적용(){
        서브_집계_기본틀(new 계층_하위_집계_빌더());
    }

    public void 형제_하위_집계_빌더_적용(){
        서브_집계_기본틀(new 비계층_하위_집계_빌더());
    }

    private void 서브_집계_기본틀(하위_집계_빌더 하위_집계_빌더){
        Function<하위_집계_빌더, AggregationBuilder> function = (a) -> a.createAggregation(하위그룹필드들,하위크기);
        Optional.ofNullable(하위그룹필드들)
            .ifPresent(하위그룹필드들->{
                if(!하위그룹필드들.isEmpty()){
                    termsAggregationBuilder
                        .subAggregation(
                            function.apply(하위_집계_빌더)
                        );
                }
            });
    }

    public NativeSearchQuery 생성(){
        return nativeSearchQueryBuilder.build();
    }
}
