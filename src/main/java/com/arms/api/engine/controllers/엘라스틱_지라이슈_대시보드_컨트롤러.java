package com.arms.api.engine.controllers;

import java.util.List;
import java.util.Map;

import com.arms.api.engine.models.*;
import com.arms.elasticsearch.util.query.*;
import com.arms.elasticsearch.util.검색결과;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.arms.api.engine.models.dashboard.bar.요구사항_지라이슈상태_주별_집계;
import com.arms.api.engine.models.dashboard.treemap.Worker;
import com.arms.api.engine.services.지라이슈_대시보드_서비스;
import com.arms.elasticsearch.util.query.bool.TermQueryMust;
import com.arms.elasticsearch.util.query.bool.TermsQueryFilter;
import com.arms.elasticsearch.util.검색결과_목록_메인;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/engine/jira/dashboard")
@Slf4j
public class 엘라스틱_지라이슈_대시보드_컨트롤러 {

    @Autowired
    private 지라이슈_대시보드_서비스 지라이슈_검색엔진;

    @ResponseBody
    @GetMapping("/jira-issue-statuses")
    public ResponseEntity<검색결과_목록_메인> 요구사항이슈집계(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청) {
        EsQuery esQuery = new EsQueryBuilder()
                .bool(  new TermQueryMust("pdServiceId", 지라이슈_제품_및_제품버전_검색요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersion", 지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks()));
        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(검색_일반_요청.of(지라이슈_제품_및_제품버전_검색요청, esQuery)));
    }

    @ResponseBody
    @GetMapping("/aggregation/nested")
    public ResponseEntity<검색결과_목록_메인> nestedAggregation(
            지라이슈_제품_및_제품버전_검색요청 검색요청
    ) {
        EsQueryBuilder esQuery = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("pdServiceId", 검색요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersion", 검색요청.getPdServiceVersionLinks())
                );

        if (검색요청.getIsReqType() == IsReqType.REQUIREMENT) {
            esQuery.bool(new TermQueryMust("isReq", true));
        } else if (검색요청.getIsReqType() == IsReqType.ISSUE) {
            esQuery.bool(new TermQueryMust("isReq", false));
        }

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(검색_일반_요청.of(검색요청, esQuery)));
    }

    @ResponseBody
    @GetMapping("/aggregation/flat")
    public ResponseEntity<검색결과_목록_메인> flatAggregation(
            지라이슈_제품_및_제품버전_검색요청 검색요청
    ) {
        EsQueryBuilder esQuery = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("pdServiceId", 검색요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersion", 검색요청.getPdServiceVersionLinks())
                );

        if (검색요청.getIsReqType() == IsReqType.REQUIREMENT) {
            esQuery.bool(new TermQueryMust("isReq", true));
        } else if (검색요청.getIsReqType() == IsReqType.ISSUE) {
            esQuery.bool(new TermQueryMust("isReq", false));
        }

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(검색_일반_요청_서브집계.of(검색요청, esQuery)));
    }

    @ResponseBody
    @GetMapping("/requirements-jira-issue-statuses")
    public ResponseEntity<Map<String, 요구사항_지라이슈상태_주별_집계>> 요구사항이슈월별집계(
            지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청
    ) {
        return ResponseEntity.ok(지라이슈_검색엔진.요구사항_지라이슈상태_주별_집계(지라이슈_제품_및_제품버전_검색요청));
    }

    @ResponseBody
    @GetMapping("/assignee-jira-issue-statuses")
    public Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별집계(
            @RequestParam Long pdServiceLink) {
        Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별_집계결과 = 지라이슈_검색엔진.담당자_요구사항여부_상태별집계(pdServiceLink);
        return 담당자_요구사항여부_상태별_집계결과;
    }

    @ResponseBody
    @GetMapping("/issue-assignee/{pdServiceId}")
    public Map<String, Long> 제품서비스별_담당자_이름_통계(
            @PathVariable("pdServiceId") Long 제품서비스_아이디) {
        return 지라이슈_검색엔진.제품서비스별_담당자_이름_통계(0L, 제품서비스_아이디);
    }

    @ResponseBody
    @GetMapping("/version-assignees")
    public ResponseEntity<List<검색결과>> 제품별_버전_및_작업자(
            지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청
    ) {
        return ResponseEntity.ok(지라이슈_검색엔진.제품_버전별_담당자_목록(지라이슈_제품_및_제품버전_검색요청));
    }

    @ResponseBody
    @GetMapping("/date/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일자별_검색(@PathVariable Long pdServiceId, 지라이슈_일자별_검색요청 지라이슈_일자별_검색요청) {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                      new TermsQueryFilter(지라이슈_일자별_검색요청.get필터필드(),지라이슈_일자별_검색요청.get필터필드검색어()),
                      new TermQueryMust("pdServiceId",pdServiceId));

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(검색_일자별_요청.of(지라이슈_일자별_검색요청, esQuery)));
    }

    @ResponseBody
    @GetMapping("/normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일반_검색(@PathVariable Long pdServiceId, 지라이슈_일반_검색요청 지라이슈_일반_검색요청) {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                         new TermQueryMust("pdServiceId",pdServiceId)
                        ,new TermQueryMust("isReq",지라이슈_일반_검색요청.getIsReq())
                );

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(검색_일반_요청.of(지라이슈_일반_검색요청, esQuery)));
    }

    @ResponseBody
    @GetMapping("/normal-version/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일반_버전필터_검색(@PathVariable Long pdServiceId,
                                                 @RequestParam List<Long> pdServiceVersionLinks,
                                                 지라이슈_일반_검색요청 지라이슈_일반_검색요청) {
        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("pdServiceId",pdServiceId)
                        ,new TermQueryMust("isReq",지라이슈_일반_검색요청.getIsReq())
                        ,new TermsQueryFilter("pdServiceVersion",pdServiceVersionLinks)
                );

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(검색_일반_요청.of(지라이슈_일반_검색요청, esQuery)));
    }

    @ResponseBody
    @GetMapping("/normal-version-only/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일반_버전필터_작업자별_검색(@PathVariable Long pdServiceId,
                                                 @RequestParam List<Long> pdServiceVersionLinks,
                                                    지라이슈_단순_검색요청 지라이슈_단순_검색_요청) {
        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("pdServiceId",pdServiceId)
                        ,new TermsQueryFilter("pdServiceVersion",pdServiceVersionLinks)
                );

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(검색_일반_요청.of(지라이슈_단순_검색_요청, esQuery)));
    }

    @ResponseBody
    @GetMapping("/normal-versionAndMail-filter/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일반_버전_및_작업자_필터_검색(@PathVariable Long pdServiceId,
                                                      @RequestParam List<Long> pdServiceVersionLinks,
                                                      @RequestParam List<String> mailAddressList,
                                                      지라이슈_단순_검색요청 지라이슈_단순_검색_요청) {
        EsQuery esQuery
                = new EsQueryBuilder()
                .bool(  new TermsQueryFilter("assignee.assignee_emailAddress.keyword", mailAddressList),
                        new TermsQueryFilter("pdServiceVersion",pdServiceVersionLinks),
                        new TermQueryMust("pdServiceId",pdServiceId)
                );

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(검색_일반_요청.of(지라이슈_단순_검색_요청, esQuery)));
    }

    @ResponseBody
    @GetMapping("/assignees-requirements-involvements")
    public ResponseEntity<List<Worker>> 작업자_별_요구사항_별_관여도(
            지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청
    ) {
        return ResponseEntity.ok(지라이슈_검색엔진.작업자_별_요구사항_별_관여도(지라이슈_제품_및_제품버전_검색요청));
    }

    @ResponseBody
    @GetMapping("/exclusion-isreq-normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 요구사항여부제외_일반_검색(@PathVariable Long pdServiceId, 지라이슈_일반_검색요청 지라이슈_일반_검색요청) {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId",pdServiceId));

        검색결과_목록_메인 집계결과_가져오기 = 지라이슈_검색엔진.집계결과_가져오기(검색_일반_요청.of(지라이슈_일반_검색요청, esQuery));
        return ResponseEntity.ok(집계결과_가져오기);
    }

    @ResponseBody
    @GetMapping("/isreq-normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 요구사항_일반_검색(@PathVariable Long pdServiceId, 지라이슈_일반_검색요청 지라이슈_일반_검색요청) {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId",pdServiceId));

        검색결과_목록_메인 집계결과_가져오기 = 지라이슈_검색엔진.집계결과_가져오기(검색_크기별_요청.of(지라이슈_일반_검색요청, esQuery));
        return ResponseEntity.ok(집계결과_가져오기);
    }

}
