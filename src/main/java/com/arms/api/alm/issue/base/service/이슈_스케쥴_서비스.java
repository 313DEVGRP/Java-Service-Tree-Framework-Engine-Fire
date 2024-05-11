package com.arms.api.alm.issue.base.service;

import com.arms.api.alm.issue.base.model.지라이슈_엔티티;

import java.util.List;

public interface 이슈_스케쥴_서비스 {

    지라이슈_엔티티 이슈_추가하기(지라이슈_엔티티 지라이슈_엔티티);

    int 대량이슈_추가하기(List<지라이슈_엔티티> 대량이슈_리스트);

    지라이슈_엔티티 이슈_조회하기(String 조회조건_아이디);


    지라이슈_엔티티 이슈_검색엔진_저장(Long 지라서버_아이디, String 이슈_키, Long 제품서비스_아이디, Long[] 제품서비스_버전_아이디들, Long cReqLink) throws Exception;

    boolean 지라이슈_인덱스백업();

    boolean 지라이슈_인덱스삭제();

    int 이슈_링크드이슈_서브테스크_벌크로_추가하기(Long 지라서버_아이디, String 이슈_키, String 프로젝트키_또는_아이디, Long 제품서비스_아이디, Long[] 제품서비스_버전들, Long cReqLink) throws Exception;

    int 증분이슈_링크드이슈_서브테스크_벌크추가(Long 지라서버_아이디, String 이슈_키, String 프로젝트키_또는_아이디, Long 제품서비스_아이디, Long[] 제품서비스_버전들, Long cReqLink) throws Exception;
}
