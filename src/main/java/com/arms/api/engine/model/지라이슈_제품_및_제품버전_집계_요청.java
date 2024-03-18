package com.arms.api.engine.model;

import com.arms.elasticsearch.base.기본_집계_요청;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 지라이슈_제품_및_제품버전_집계_요청 extends 기본_집계_요청 {
    private Long pdServiceLink;
    private Long[] pdServiceVersionLinks;
    private IsReqType isReqType;

}
