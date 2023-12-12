package com.arms.api.engine.services;

import java.util.List;
import java.util.Map;

import com.arms.api.engine.dtos.트리맵_담당자_요구사항_기여도;
import com.arms.api.engine.dtos.일자별_요구사항_연결된이슈_생성개수_및_상태데이터;
import com.arms.api.engine.dtos.요구사항_지라이슈상태_주별_집계;
import com.arms.api.engine.models.지라이슈;
import com.arms.api.engine.models.지라이슈_일자별_제품_및_제품버전_검색요청;
import com.arms.api.engine.models.지라이슈_제품_및_제품버전_검색요청;
import com.arms.elasticsearch.util.query.쿼리_추상_팩토리;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색결과_목록_메인;

public interface 지라이슈_대시보드_서비스 {


    Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별집계(Long pdServiceLink);

    Map<String, Long> 제품서비스별_담당자_이름_통계(Long 지라서버_아이디, Long 제품서비스_아이디);

    검색결과_목록_메인 집계결과_가져오기(쿼리_추상_팩토리 쿼리추상팩토리);

    List<검색결과> 제품_버전별_담당자_목록(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청);

    List<트리맵_담당자_요구사항_기여도> 작업자_별_요구사항_별_관여도(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청);

    Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항_지라이슈상태_주별_집계(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청);

    List<지라이슈> 제품서비스_버전목록으로_주간_업데이트된_이슈조회(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청, Integer baseWeek);

    Map<String, 일자별_요구사항_연결된이슈_생성개수_및_상태데이터> 지라이슈_기준일자별_제품_및_제품버전_집계검색(지라이슈_일자별_제품_및_제품버전_검색요청 지라이슈_일자별_제품_및_제품버전_검색요청);
}
