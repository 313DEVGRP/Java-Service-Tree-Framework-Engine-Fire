package com.arms.api.alm.requirement.controller;


import com.arms.api.alm.requirement.service.요구사항_서비스;
import com.arms.api.alm.issue.base.model.지라이슈_엔티티;
import com.arms.api.util.model.dto.지라이슈_일반_집계_요청;
import com.arms.api.util.model.dto.지라이슈_제품_및_제품버전_집계_요청;
import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.esquery.EsQueryBuilder;
import com.arms.elasticsearch.query.esquery.esboolquery.must.MustTermQuery;
import com.arms.elasticsearch.query.factory.creator.old.일반_검색_쿼리_생성기;
import com.arms.elasticsearch.query.filter.TermsQueryFilter;
import com.arms.elasticsearch.버킷_집계_결과;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/engine/jira/dashboard")
public class 요구사항_컨트롤러 {

    @Autowired
    private 요구사항_서비스 요구사항_서비스;

    // Requirement - ReqStatus
    @PostMapping("/requirement-linkedissue/{pdServiceId}")
    public ResponseEntity<List<지라이슈_엔티티>> 제품별_요구사항_연결이슈_조회(@PathVariable Long pdServiceId, 지라이슈_일반_집계_요청 지라이슈_일반_집계_요청) {

        EsQuery esQuery
                = new EsQueryBuilder()
                .bool(
                        new MustTermQuery("pdServiceId",pdServiceId),
                        new TermsQueryFilter("pdServiceVersions",지라이슈_일반_집계_요청.getPdServiceVersionLinks())
                );

        return ResponseEntity.ok(요구사항_서비스.지라이슈_조회(일반_검색_쿼리_생성기.of(지라이슈_일반_집계_요청, esQuery)));
    }

    // Requirement - ReqStatus, Dashboard, Detail_Dashboard
    @GetMapping("/issue-assignee/{pdServiceId}")
    public Map<String, Long> 제품서비스별_담당자_이름_통계(
            @PathVariable("pdServiceId") Long 제품서비스_아이디) {
        return 요구사항_서비스.제품서비스별_담당자_이름_통계(0L, 제품서비스_아이디);
    }

    /* 통합으로 변경가능 API */
    // Requirement - ReqStatus, ReqGantt
    @ResponseBody
    @GetMapping("/getProgress/{pdServiceId}")
    public Map<String, Long> 제품서비스_버전별_상태값_통계(@PathVariable("pdServiceId") Long 제품서비스_아이디,
                                                            @RequestParam("pdServiceVersions") Long[] 제품서비스_버전_아이디) throws IOException {

        return 요구사항_서비스.제품서비스_버전별_상태값_통계(제품서비스_아이디,제품서비스_버전_아이디);
    }


    @GetMapping("/req-assignees")
    public ResponseEntity<List<버킷_집계_결과>> 제품_요구사항별_담당자_목록(
            지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청
    ) {
        return ResponseEntity.ok(요구사항_서비스.제품_요구사항별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청));
    }

}

