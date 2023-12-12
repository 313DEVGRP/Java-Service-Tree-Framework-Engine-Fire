package com.arms.api.engine.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 지라이슈_일자별_제품_및_제품버전_검색요청 extends 지라이슈_제품_및_제품버전_검색요청 {
    private String 일자기준;
    private int 날짜페이지 = 1;
    private int 날짜크기 = 30;
}
