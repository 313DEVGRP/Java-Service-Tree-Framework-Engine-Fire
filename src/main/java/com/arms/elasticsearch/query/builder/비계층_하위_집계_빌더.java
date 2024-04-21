package com.arms.elasticsearch.query.builder;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.List;

public class 비계층_하위_집계_빌더 implements 하위_집계_빌더{


    /**
     * 인자로 받은 하위_그룹필드들 값을 모두 메인 집계 하위에 추가하고 싶은 케이스 대응
     * For example,
     * 담당자 별 (1 depth)
     *  - 요구사항 개수 (2 depth)
     *  - 이슈 개수 (2 depth)
     *  - 이슈 상태 (2 depth)
     *  - 이슈 우선순위 (2 depth)
     *
     * @param 하위_그룹필드들 The list of sub-group fields to be aggregated.
     * @param size The maximum number of unique sub-group field values to be included in the aggregation.
     * @return An {@link AggregationBuilder} that can be used to execute the aggregation.
     */
    @Override
    public AggregationBuilder createAggregation(List<String> 하위그룹필드, int size) {
        AggregationBuilder mainAggregation = null;
        for (String 하위필드명 : 하위그룹필드) {
            AggregationBuilder agg = AggregationBuilders.terms("group_by_" + 하위필드명)
                    .field(하위필드명)
                    .size(size);
            if (mainAggregation == null) {
                mainAggregation = agg;
            } else {
                mainAggregation.subAggregation(agg);
            }
        }
        return mainAggregation;
    }
}
