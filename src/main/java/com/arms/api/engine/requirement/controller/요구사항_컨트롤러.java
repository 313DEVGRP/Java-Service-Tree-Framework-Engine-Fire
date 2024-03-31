package com.arms.api.engine.requirement.controller;


import com.arms.api.engine.jiraissue.entity.지라이슈;
import com.arms.api.engine.model.dto.지라이슈_일반_집계_요청;
import com.arms.api.engine.requirement.service.요구사항_서비스;
import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.esquery.EsQueryBuilder;
import com.arms.elasticsearch.query.esquery.esboolquery.must.MustTermQuery;
import com.arms.elasticsearch.query.factory.일반_검색_쿼리_생성기;
import com.arms.elasticsearch.query.filter.TermsQueryFilter;
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
    public ResponseEntity<List<지라이슈>> 제품별_요구사항_연결이슈_조회(@PathVariable Long pdServiceId, 지라이슈_일반_집계_요청 지라이슈_일반_집계_요청) {

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
    public Map<String, Long> 제품서비스_버전별_상태값_통계(@PathVariable("connectId") Long 지라서버_아이디,
                                              @PathVariable("pdServiceId") Long 제품서비스_아이디,
                                              @RequestParam("pdServiceVersions") Long[] 제품서비스_버전_아이디) throws IOException {

        return 요구사항_서비스.제품서비스_버전별_상태값_통계(제품서비스_아이디,제품서비스_버전_아이디);
    }

    // Requirement - ReqStatus
    @ResponseBody
    @RequestMapping(
            value = {"/search/{issueKey}/subAndLinks"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈> 요구사항_링크드이슈_서브테스크_검색하기(@PathVariable("connectId") Long 지라서버_아이디,
                                            @PathVariable("issueKey") String 이슈_키,
                                            @RequestParam("page") int 페이지_번호,
                                            @RequestParam("size") int 페이지_사이즈 ) {

        return 요구사항_서비스.요구사항_링크드이슈_서브테스크_검색하기(지라서버_아이디, 이슈_키, 페이지_번호, 페이지_사이즈);
    }

}

