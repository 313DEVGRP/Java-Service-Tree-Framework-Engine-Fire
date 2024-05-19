package com.arms.elasticsearch.query.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
public class 하위_집계 {
    private String 별칭;
    private String 필드명;
    @Builder.Default
    private boolean 결과_갯수_기준_오름차순 = false;
    @Builder.Default
    private int 크기 = 1000;


}
