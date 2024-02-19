package com.arms.api.engine.services;

import java.util.List;
import java.util.Map;

import com.arms.api.engine.dtos.*;
import com.arms.api.engine.models.지라이슈;
import com.arms.api.engine.models.지라이슈_일자별_제품_및_제품버전_집계_요청;
import com.arms.api.engine.models.지라이슈_제품_및_제품버전_집계_요청;
import com.arms.api.engine.vo.제품_서비스_버전;
import com.arms.elasticsearch.util.query.쿼리_추상_팩토리;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색결과_목록_메인;

public interface 지라이슈_대시보드_서비스 {


    Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별집계(Long pdServiceLink);

    Map<String, Long> 제품서비스별_담당자_이름_통계(Long 지라서버_아이디, Long 제품서비스_아이디);

    검색결과_목록_메인 집계결과_가져오기(쿼리_추상_팩토리 쿼리추상팩토리);

    List<지라이슈> 지라이슈_조회(쿼리_추상_팩토리 쿼리추상팩토리);

    List<검색결과> 제품_버전별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청);

    List<Worker> 작업자_별_요구사항_별_관여도(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청);

    Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항_지라이슈상태_주별_집계(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청);

    Map<String, 일자별_요구사항_연결된이슈_생성개수_및_상태데이터> 지라이슈_기준일자별_제품_및_제품버전_집계검색(지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청);

    List<지라이슈> 지라이슈_기준일자별_제품_및_제품버전_업데이트된_이슈조회(지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청);

    List<제품_서비스_버전> 요구사항_별_상태_및_관여_작업자수_내용(검색결과_목록_메인 요구사항, 검색결과_목록_메인 하위이슈);


    Map<Long, Map<String, Map<String,List<요구사항_별_업데이트_데이터>>>>  요구사항별_업데이트_능선_데이터(지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청);

    List<검색결과> 제품_버전별_요구사항별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청);

    List<지라이슈> 요구사항키로_하위이슈_조회(String 지라키);

    Map<String,List<요구사항_지라이슈키별_업데이트_목록_데이터>> 요구사항_지라이슈키별_업데이트_목록(List<String> 지라키_목록);
}
