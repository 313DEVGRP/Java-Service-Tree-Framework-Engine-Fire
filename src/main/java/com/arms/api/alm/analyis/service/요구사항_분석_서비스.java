package com.arms.api.alm.analyis.service;

import java.util.List;
import java.util.Map;

import com.arms.api.alm.issue.base.model.지라이슈_엔티티;
import com.arms.api.util.model.dto.*;
import com.arms.elasticsearch.query.factory.creator.query.쿼리_생성기;
import com.arms.elasticsearch.query.model.vo.버킷_집계_결과;
import com.arms.elasticsearch.query.model.vo.버킷_집계_결과_목록_합계;

public interface 요구사항_분석_서비스 {

    버킷_집계_결과_목록_합계 집계결과_가져오기(쿼리_생성기 쿼리_생성기);

    List<버킷_집계_결과> 제품_버전별_담당자_목록(지라이슈_제품_및_제품버전_검색__집계_하위_요청 지라이슈_제품_및_제품버전_집계_요청);

    Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항_지라이슈상태_주별_집계(지라이슈_제품_및_제품버전_검색__집계_하위_요청 지라이슈_제품_및_제품버전_집계_요청);

    Map<String, 일자별_요구사항_연결된이슈_생성개수_및_상태데이터> 지라이슈_기준일자별_제품_및_제품버전_집계검색(지라이슈_일자별_제품_및_제품버전_검색__집계_하위_요청 지라이슈_일자별_제품_및_제품버전_집계_요청);

    List<지라이슈_엔티티> 지라이슈_기준일자별_제품_및_제품버전_업데이트된_이슈조회(지라이슈_일자별_제품_및_제품버전_검색__집계_하위_요청 지라이슈_일자별_제품_및_제품버전_집계_요청);

    Map<Long, Map<String, Map<String,List<요구사항_별_업데이트_데이터>>>>  요구사항별_업데이트_능선_데이터(지라이슈_일자별_제품_및_제품버전_검색__집계_하위_요청 지라이슈_일자별_제품_및_제품버전_집계_요청);

    List<버킷_집계_결과> 제품_버전별_요구사항별_담당자_목록(지라이슈_제품_및_제품버전_검색__집계_하위_요청 지라이슈_제품_및_제품버전_집계_요청);

    Map<String,List<요구사항_지라이슈키별_업데이트_목록_데이터>> 요구사항_지라이슈키별_업데이트_목록(List<String> 지라키_목록);

    List<요구사항_버전_이슈_키_상태_작업자수> 버전별_요구사항_상태_및_관여_작업자수_내용(Long pdServiceLink, Long[] pdServiceVersionLinks);

    List<지라이슈_엔티티> 제품서비스_버전목록으로_조회(Long pdServiceLink, Long[] pdServiceVersionLinks);//

    히트맵데이터 히트맵_제품서비스_버전목록으로_조회(Long pdServiceLink, Long[] pdServiceVersionLinks);//


}
