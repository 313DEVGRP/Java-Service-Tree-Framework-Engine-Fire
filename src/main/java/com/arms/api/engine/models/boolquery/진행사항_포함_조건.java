package com.arms.api.engine.models.boolquery;

import com.arms.elasticsearch.util.query.bool.조건_쿼리_컴포넌트;
import com.arms.elasticsearch.util.query.bool.조건_쿼리_컴포넌트_요소;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.util.ObjectUtils;

@Builder
@AllArgsConstructor
public class 진행사항_포함_조건 extends 조건_쿼리_컴포넌트_요소 {

    private String 포함필드;
    private Object 포함필드검색어;

    private 조건_쿼리_컴포넌트 조건_쿼리_컴포넌트;

    public BoolQueryBuilder getBoolQuery() {
        super.조건_쿼리_컴포넌트 = this.조건_쿼리_컴포넌트;
        if(!ObjectUtils.isEmpty(포함필드)) {
            return this.조건_쿼리_컴포넌트.getBoolQuery().must(QueryBuilders.termQuery(포함필드, 포함필드검색어));
        }
        return this.조건_쿼리_컴포넌트.getBoolQuery();
    }
}
