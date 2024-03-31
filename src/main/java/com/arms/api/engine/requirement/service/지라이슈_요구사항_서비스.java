package com.arms.api.engine.requirement.service;

import com.arms.api.engine.jiraissue.entity.지라이슈;
import com.arms.api.engine.model.dto.*;
import com.arms.api.engine.model.vo.Worker;
import com.arms.elasticsearch.query.쿼리_추상_팩토리;
import com.arms.elasticsearch.버킷_집계_결과;
import com.arms.elasticsearch.버킷_집계_결과_목록_합계;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface 지라이슈_요구사항_서비스 {

    Map<String, Long> 제품서비스별_담당자_이름_통계(Long 지라서버_아이디, Long 제품서비스_아이디);
    List<지라이슈> 지라이슈_조회(쿼리_추상_팩토리 쿼리추상팩토리);

    Map<String, Long> 제품서비스_버전별_상태값_통계(Long 제품서비스_아이디, Long[] 버전_아이디) throws IOException;

    List<지라이슈> 요구사항_링크드이슈_서브테스크_검색하기(Long 서버_아이디, String 이슈_키, int 페이지_번호, int 페이지_사이즈);


}
