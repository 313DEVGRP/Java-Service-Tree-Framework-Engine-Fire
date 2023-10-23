package com.arms.elasticsearch.services;

import com.arms.elasticsearch.models.SankeyElasticSearchData;
import com.arms.elasticsearch.models.요구사항_지라이슈상태_월별_집계;
import com.arms.elasticsearch.models.집계_응답;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface 지라이슈_대시보드_서비스 {

    public List<집계_응답> 이슈상태집계(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException;

    public Map<String, 요구사항_지라이슈상태_월별_집계> 요구사항_지라이슈상태_월별_집계(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException;

    public Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별집계(Long pdServiceLink) throws IOException;

    public Map<String, Long> 제품서비스별_담당자_이름_통계(Long 지라서버_아이디, Long 제품서비스_아이디) throws IOException;
    public Map<String, List<SankeyElasticSearchData>> 제품_버전별_담당자_목록(Long pdServiceLink, List<Long> pdServiceVersionLinks) throws IOException;

}