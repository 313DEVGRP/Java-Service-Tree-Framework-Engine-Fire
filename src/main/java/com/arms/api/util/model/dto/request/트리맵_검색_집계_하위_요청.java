package com.arms.api.util.model.dto.request;


import java.util.List;

import com.arms.api.util.model.enums.IsReqType;
import com.arms.egovframework.javaservice.esframework.model.dto.기본_검색_집계_하위_요청;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class 트리맵_검색_집계_하위_요청 extends 기본_검색_집계_하위_요청 {
    private Long pdServiceLink;
    private Long[] pdServiceVersionLinks;
    private List<com.arms.api.util.model.dto.response.제품버전목록> 제품버전목록;
    private IsReqType isReqType;
}
