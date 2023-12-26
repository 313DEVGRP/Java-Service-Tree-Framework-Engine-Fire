package com.arms.api.engine.models;

import com.arms.elasticsearch.util.base.기본_집계_요청;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class 지라이슈_제품_및_제품버전_집계_요청 extends 기본_집계_요청 {
    private Long pdServiceLink;
    private List<Long> pdServiceVersionLinks;
    private IsReqType isReqType;

}
