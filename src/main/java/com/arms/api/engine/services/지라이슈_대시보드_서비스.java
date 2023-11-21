package com.arms.api.engine.services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.arms.api.engine.models.dashboard.bar.요구사항_지라이슈상태_주별_집계;
import com.arms.api.engine.models.dashboard.sankey.SankeyElasticSearchData;
import com.arms.api.engine.models.dashboard.treemap.Worker;
import com.arms.elasticsearch.util.query.쿼리_추상_팩토리;
import com.arms.elasticsearch.util.검색결과_목록_메인;

public interface 지라이슈_대시보드_서비스 {


    public Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별집계(Long pdServiceLink) throws IOException;

    public Map<String, Long> 제품서비스별_담당자_이름_통계(Long 지라서버_아이디, Long 제품서비스_아이디) throws IOException;

    검색결과_목록_메인 집계결과_가져오기(쿼리_추상_팩토리 쿼리추상팩토리) throws IOException;

    Map<String, List<SankeyElasticSearchData>> 제품_버전별_담당자_목록(Long pdServiceLink, List<Long> pdServiceVersionLinks, int maxResults) throws IOException;

    List<Worker> 작업자_별_요구사항_별_관여도(Long pdServiceLink, List<Long> pdServiceVersionLinks, int maxResults) throws IOException;

    Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항_지라이슈상태_주별_집계(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException;
}
