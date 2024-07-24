package com.arms.api.alm.issue.base.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Builder
@Getter
@Slf4j
@지라이슈_벌크_추가_요청_유효성
public class 지라이슈_벌크_추가_요청 {

    private Long 지라서버_아이디;

    private String 이슈_키;

    private String 프로젝트키_또는_아이디;

    private Long 제품서비스_아이디;

    private Long[] 제품서비스_버전들;

    private Long cReqLink;


}
