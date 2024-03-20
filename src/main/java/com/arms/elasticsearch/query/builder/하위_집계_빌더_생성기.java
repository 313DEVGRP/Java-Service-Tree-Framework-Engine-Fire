package com.arms.elasticsearch.query.builder;

import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.List;

public class 하위_집계_빌더_생성기 {

    private List<String> 하위그룹필드;
    private int size;

    public 하위_집계_빌더_생성기(List<String> 하위그룹필드, int size){
        this.하위그룹필드 = 하위그룹필드;
    }

    public AggregationBuilder createNestedAggregation(List<String> 하위_그룹필드들, int size) {
        return
                하위_그룹필드들
                        .stream()
                        .map(groupField ->
                                AggregationBuilders.terms("group_by_" + groupField)
                                        .field(groupField)
                                        .size(size))
                        .reduce(null, (agg1, agg2) -> {
                            if (agg1 == null) {
                                return agg2;
                            } else {
                                return agg1.subAggregation(agg2);
                            }
                        });
    }

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
    public AggregationBuilder createFlatAggregation(List<String> 하위_그룹필드들, int size) {
        AggregationBuilder mainAggregation = null;
        for (String 하위필드명 : 하위_그룹필드들) {
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
