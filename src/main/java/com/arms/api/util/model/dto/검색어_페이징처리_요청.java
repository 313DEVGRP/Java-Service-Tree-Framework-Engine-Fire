package com.arms.api.util.model.dto;

import com.arms.elasticsearch.query.base.일반_검색_요청;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 검색어_페이징처리_요청 extends 일반_검색_요청 {

    private String 검색어;
}
