package com.arms.api.engine.analyis.service;

import java.util.List;
import java.util.Map;

import com.arms.api.engine.model.dto.*;
import com.arms.api.engine.model.vo.Worker;
import com.arms.api.engine.model.vo.검색어_검색결과;
import com.arms.api.engine.jiraissue.entity.지라이슈;
import com.arms.api.engine.model.dto.지라이슈_일자별_제품_및_제품버전_집계_요청;
import com.arms.api.engine.model.dto.지라이슈_제품_및_제품버전_집계_요청;
import com.arms.api.engine.model.dto.트리맵_집계_요청;
import com.arms.api.engine.model.vo.제품_서비스_버전;
import com.arms.elasticsearch.query.쿼리_추상_팩토리;
import com.arms.elasticsearch.버킷_집계_결과;
import com.arms.elasticsearch.버킷_집계_결과_목록_합계;
import org.springframework.data.elasticsearch.core.SearchHit;

public interface 지라이슈_대시보드_서비스 {

    버킷_집계_결과_목록_합계 집계결과_가져오기(쿼리_추상_팩토리 쿼리추상팩토리);

    List<버킷_집계_결과> 제품_버전별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청);

    List<Worker> 작업자_별_요구사항_별_관여도(트리맵_집계_요청 트리맵_집계_요청);

    Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항_지라이슈상태_주별_집계(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청);

    Map<String, 일자별_요구사항_연결된이슈_생성개수_및_상태데이터> 지라이슈_기준일자별_제품_및_제품버전_집계검색(지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청);

    List<지라이슈> 지라이슈_기준일자별_제품_및_제품버전_업데이트된_이슈조회(지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청);

    Map<Long, Map<String, Map<String,List<요구사항_별_업데이트_데이터>>>>  요구사항별_업데이트_능선_데이터(지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청);

    List<버킷_집계_결과> 제품_버전별_요구사항별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청);

    Map<String,List<요구사항_지라이슈키별_업데이트_목록_데이터>> 요구사항_지라이슈키별_업데이트_목록(List<String> 지라키_목록);

    List<요구사항_버전_이슈_키_상태_작업자수> 버전별_요구사항_상태_및_관여_작업자수_내용(Long pdServiceLink, Long[] pdServiceVersionLinks);

}
