package com.arms.api.alm.issue.base.model.vo;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;

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

    private Long 지라서버_아이디;//7804172536102413171

    private String 이슈_키;//KAN-105

    private String 프로젝트키_또는_아이디;//KAN

    private Long 제품서비스_아이디;//22

    private Long[] 제품서비스_버전들;//55

    private Long cReqLink;//172

    public String 조회조건_아이디(){
        return 지라서버_아이디 + "_" + 프로젝트키_또는_아이디 + "_" + 이슈_키;
    }

    public String 조회조건_아이디(지라이슈_데이터 지라이슈_데이터){
        return 지라서버_아이디 + "_" + 프로젝트키_또는_아이디 + "_" + 지라이슈_데이터.getId();
    }


}
