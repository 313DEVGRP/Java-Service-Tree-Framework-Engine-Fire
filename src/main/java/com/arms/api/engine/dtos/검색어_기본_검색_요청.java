package com.arms.api.engine.dtos;

import com.arms.elasticsearch.util.base.기본_검색_요청;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 검색어_기본_검색_요청 extends 기본_검색_요청 {

    private String 검색어;
}
