package com.arms.api.utils.model.dto;

import com.arms.elasticsearch.query.base.기본_집계_요청;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 검색어_집계_요청 extends 기본_집계_요청 {

    String 검색어;
    String 시작_날짜;
    String 끝_날짜;
}
