package com.arms.api.engine.dashboard.service;

import com.arms.api.engine.jiraissue.entity.지라이슈;
import com.arms.api.engine.model.vo.히트맵데이터;
import com.arms.elasticsearch.검색조건;

import java.io.IOException;
import java.util.List;
import java.util.Map;

 public interface 지라이슈_서비스 {

     지라이슈 이슈_추가하기(지라이슈 지라이슈);

     int 대량이슈_추가하기(List<지라이슈> 대량이슈_리스트);


     지라이슈 이슈_조회하기(String 조회조건_아이디);

     List<지라이슈> 이슈_검색하기(검색조건 검색조건);

     지라이슈 이슈_검색엔진_저장(Long 지라서버_아이디, String 이슈_키, Long 제품서비스_아이디, Long[] 제품서비스_버전_아이디들, Long cReqLink) throws Exception;

     boolean 지라이슈_인덱스백업();

     boolean 지라이슈_인덱스삭제();

     int 이슈_링크드이슈_서브테스크_벌크로_추가하기(Long 지라서버_아이디, String 이슈_키, Long 제품서비스_아이디, Long[] 제품서비스_버전들, Long cReqLink) throws Exception;

     int 증분이슈_링크드이슈_서브테스크_벌크추가(Long 지라서버_아이디, String 이슈_키, Long 제품서비스_아이디, Long[] 제품서비스_버전들, Long cReqLink) throws Exception;


    List<지라이슈> 제품서비스_버전목록으로_조회(Long pdServiceLink, Long[] pdServiceVersionLinks);

    히트맵데이터 히트맵_제품서비스_버전목록으로_조회(Long pdServiceLink, Long[] pdServiceVersionLinks);


 }
