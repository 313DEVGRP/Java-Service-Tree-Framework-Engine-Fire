package com.arms.api.util.model.dto;

import com.arms.egovframework.javaservice.esframework.model.dto.기본_검색_집계_하위_요청;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 검색어_검색_집계_하위_요청 extends 기본_검색_집계_하위_요청 {

    String 시작_날짜;
    String 끝_날짜;
}
