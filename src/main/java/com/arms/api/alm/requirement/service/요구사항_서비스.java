package com.arms.api.alm.requirement.service;

import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.util.model.dto.request.지라이슈_기본_검색_집계_하위_요청;
import com.arms.egovframework.javaservice.esframework.factory.creator.query.쿼리_생성기;
import com.arms.api.util.model.dto.request.지라이슈_제품_및_제품버전_검색_집계_하위_요청;
import com.arms.egovframework.javaservice.esframework.model.vo.버킷_집계_결과;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface 요구사항_서비스 {

    List<지라이슈_엔티티> 제품별_요구사항_연결이슈_조회(Long pdServiceId, 지라이슈_기본_검색_집계_하위_요청 지라이슈_일반_집계_요청);

    Map<String, Long> 제품서비스별_담당자_이름_통계(Long 지라서버_아이디, Long 제품서비스_아이디);
    List<지라이슈_엔티티> 지라이슈_조회(쿼리_생성기 쿼리_생성기);

    Map<String, Long> 제품서비스_버전별_상태값_통계(Long 제품서비스_아이디, Long[] 버전_아이디) throws IOException;

    List<버킷_집계_결과> 제품_요구사항별_담당자_목록(지라이슈_제품_및_제품버전_검색_집계_하위_요청 지라이슈_제품_및_제품버전_집계_요청);

    List<지라이슈_엔티티> 요구사항이슈_연결하위이슈_조회(Long 제품서비스_아이디, Long[] 버전_아이디, String 지라서버아이디, String 이슈키);
    List<지라이슈_엔티티> 함께_생성된_요구사항_이슈목록(Long 제품서비스_아이디, Long[] 버전_아이디, Long 요구사항_아이디);

    List<지라이슈_엔티티> 제품_버전별_삭제된_이슈조회(Long 제품서비스_아이디, Long[] 버전_아이디);

    int 이슈_삭제철회(List<지라이슈_엔티티> 삭제_철회대상_목록) throws Exception;
}
