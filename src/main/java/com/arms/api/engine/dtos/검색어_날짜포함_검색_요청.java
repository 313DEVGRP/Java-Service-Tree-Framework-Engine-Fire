package com.arms.api.engine.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class 검색어_날짜포함_검색_요청 extends 검색어_기본_검색_요청 {

    private String 시작_날짜;
    private String 끝_날짜;
}
