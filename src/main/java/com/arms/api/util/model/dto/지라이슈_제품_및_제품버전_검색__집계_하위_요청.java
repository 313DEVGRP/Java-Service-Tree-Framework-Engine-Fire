package com.arms.api.util.model.dto;

import com.arms.api.util.model.enums.IsReqType;
import com.arms.egovframework.javaservice.esframework.model.dto.기본_검색_집계_하위_요청;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 지라이슈_제품_및_제품버전_검색__집계_하위_요청 extends 기본_검색_집계_하위_요청 {
    private Long pdServiceLink;
    private Long[] pdServiceVersionLinks;
    private IsReqType isReqType;

}
