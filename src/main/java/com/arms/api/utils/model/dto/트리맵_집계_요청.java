package com.arms.api.utils.model.dto;


import java.util.List;

import com.arms.api.utils.model.enums.IsReqType;
import com.arms.elasticsearch.query.base.기본_집계_요청;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class 트리맵_집계_요청 extends 기본_집계_요청 {
    private Long pdServiceLink;
    private Long[] pdServiceVersionLinks;
    private List<com.arms.api.utils.model.dto.제품버전목록> 제품버전목록;
    private IsReqType isReqType;
}
