package com.arms.egovframework.javaservice.esframework.factory.builder;

import com.arms.egovframework.javaservice.esframework.model.dto.집계_하위_요청;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.search.aggregations.BucketOrder.count;

public class 중첩_집계_단일_빌더 {

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

    public List<AggregationBuilder> createAggregation(List<집계_하위_요청> 하위그룹필드, int size) {
        return 하위그룹필드.stream()
            .map(하위필드명->
                    AggregationBuilders.terms(Optional.ofNullable(하위필드명.get하위_필드명_별칭()).orElseGet(()->"group_by_"+하위필드명.get하위_필드명()))
                        .field(하위필드명.get하위_필드명())
                        .order(count(하위필드명.is결과_갯수_기준_오름차순()))
                        .size(size)).collect(Collectors.toList());
    }
}
