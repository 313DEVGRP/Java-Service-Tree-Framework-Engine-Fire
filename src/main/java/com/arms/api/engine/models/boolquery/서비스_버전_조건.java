package com.arms.api.engine.models.boolquery;

import com.arms.elasticsearch.util.query.bool.조건_쿼리_컴포넌트;
import com.arms.elasticsearch.util.query.bool.조건_쿼리_컴포넌트_요소;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.commons.lang3.ObjectUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;

@Builder
@AllArgsConstructor
public class 서비스_버전_조건 extends 조건_쿼리_컴포넌트_요소 {

    private Long pdServiceId;
    private List<Long> pdServiceVersion;
    private 조건_쿼리_컴포넌트 조건_쿼리_컴포넌트;

    @Override
    public BoolQueryBuilder getBoolQuery() {
        super.조건_쿼리_컴포넌트 = this.조건_쿼리_컴포넌트;
        if(!ObjectUtils.isEmpty(pdServiceId) && !ObjectUtils.isEmpty(pdServiceVersion)){
            return this.조건_쿼리_컴포넌트.getBoolQuery()
                    .must(QueryBuilders.termQuery("pdServiceId", pdServiceId))
                    .filter(QueryBuilders.termsQuery("pdServiceVersion", pdServiceVersion));
        }
        return this.조건_쿼리_컴포넌트.getBoolQuery();
    }
}
