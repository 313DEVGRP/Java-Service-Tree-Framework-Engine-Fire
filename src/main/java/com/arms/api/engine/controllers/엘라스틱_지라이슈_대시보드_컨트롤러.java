package com.arms.api.engine.controllers;

import java.util.*;
import java.util.stream.Stream;

import com.arms.api.engine.dtos.*;
import com.arms.api.engine.models.*;
import com.arms.api.engine.dtos.일자별_요구사항_연결된이슈_생성개수_및_상태데이터;
import com.arms.api.engine.vo.제품_서비스_버전;
import com.arms.elasticsearch.util.query.*;
import com.arms.elasticsearch.util.query.bool.*;
import com.arms.elasticsearch.util.검색결과;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.arms.api.engine.services.지라이슈_대시보드_서비스;
import com.arms.elasticsearch.util.검색결과_목록_메인;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/engine/jira/dashboard")
@Slf4j
public class 엘라스틱_지라이슈_대시보드_컨트롤러 {

    @Autowired
    private 지라이슈_대시보드_서비스 지라이슈_검색엔진;

    @GetMapping("/aggregation/nested")
    public ResponseEntity<검색결과_목록_메인> nestedAggregation(
            지라이슈_제품_및_제품버전_집계_요청 검색요청
    ) {
        EsBoolQuery[] esBoolQueries = Stream.of(
                new TermQueryMust("pdServiceId", 검색요청.getPdServiceLink()),
                new TermsQueryFilter("pdServiceVersions", 검색요청.getPdServiceVersionLinks()),
                검색요청.getIsReqType() == IsReqType.REQUIREMENT ? new TermQueryMust("isReq", true) : null,
                검색요청.getIsReqType() == IsReqType.ISSUE ? new TermQueryMust("isReq", false) : null
        ).filter(Objects::nonNull).toArray(EsBoolQuery[]::new);

        EsQueryBuilder esQuery = new EsQueryBuilder().bool(esBoolQueries);

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(일반_집계_요청.of(검색요청, esQuery)));
    }

    @GetMapping("/aggregation/flat")
    public ResponseEntity<검색결과_목록_메인> flatAggregation(
            지라이슈_제품_및_제품버전_집계_요청 검색요청
    ) {
        EsBoolQuery[] esBoolQueries = Stream.of(
                new TermQueryMust("pdServiceId", 검색요청.getPdServiceLink()),
                new TermsQueryFilter("pdServiceVersions", 검색요청.getPdServiceVersionLinks()),
                검색요청.getIsReqType() == IsReqType.REQUIREMENT ? new TermQueryMust("isReq", true) : null,
                검색요청.getIsReqType() == IsReqType.ISSUE ? new TermQueryMust("isReq", false) : null
        ).filter(Objects::nonNull).toArray(EsBoolQuery[]::new);

        EsQueryBuilder esQuery = new EsQueryBuilder().bool(esBoolQueries);

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(일반_집계_요청_서브집계.of(검색요청, esQuery)));
    }

    @GetMapping("/requirements-jira-issue-statuses")
    public ResponseEntity<Map<String, 요구사항_지라이슈상태_주별_집계>> 요구사항이슈월별집계(
            지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청
    ) {
        return ResponseEntity.ok(지라이슈_검색엔진.요구사항_지라이슈상태_주별_집계(지라이슈_제품_및_제품버전_집계_요청));
    }

    @GetMapping("/assignee-jira-issue-statuses")
    public Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별집계(
            @RequestParam Long pdServiceLink) {
        Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별_집계결과 = 지라이슈_검색엔진.담당자_요구사항여부_상태별집계(pdServiceLink);
        return 담당자_요구사항여부_상태별_집계결과;
    }

    @GetMapping("/issue-assignee/{pdServiceId}")
    public Map<String, Long> 제품서비스별_담당자_이름_통계(
            @PathVariable("pdServiceId") Long 제품서비스_아이디) {
        return 지라이슈_검색엔진.제품서비스별_담당자_이름_통계(0L, 제품서비스_아이디);
    }

    @GetMapping("/version-assignees")
    public ResponseEntity<List<검색결과>> 제품별_버전_및_작업자(
            지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청
    ) {
        return ResponseEntity.ok(지라이슈_검색엔진.제품_버전별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청));
    }

    @GetMapping("/date/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일자별_검색(@PathVariable Long pdServiceId, 지라이슈_일자별_집계_요청 지라이슈_일자별_집계_요청) {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                      new TermsQueryFilter(지라이슈_일자별_집계_요청.get필터필드(), 지라이슈_일자별_집계_요청.get필터필드검색어()),
                      new TermQueryMust("pdServiceId",pdServiceId));

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(일자별_집계_요청.of(지라이슈_일자별_집계_요청, esQuery)));
    }

    @GetMapping("/normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일반_검색(@PathVariable Long pdServiceId, 지라이슈_일반_집계_요청 지라이슈_일반_집계_요청) {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                         new TermQueryMust("pdServiceId",pdServiceId)
                        ,new TermQueryMust("isReq", 지라이슈_일반_집계_요청.getIsReq())
                );

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(일반_집계_요청.of(지라이슈_일반_집계_요청, esQuery)));
    }

    @GetMapping("/normal-version/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일반_버전필터_검색(@PathVariable Long pdServiceId,
                                                 @RequestParam List<Long> pdServiceVersionLinks,
                                                 지라이슈_일반_집계_요청 지라이슈_일반_집계_요청) {
        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("pdServiceId",pdServiceId)
                        ,new TermQueryMust("isReq", 지라이슈_일반_집계_요청.getIsReq())
                        ,new TermsQueryFilter("pdServiceVersions",pdServiceVersionLinks)
                );

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(일반_집계_요청.of(지라이슈_일반_집계_요청, esQuery)));
    }

    @GetMapping("/normal-version-only/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일반_버전필터_작업자별_검색(@PathVariable Long pdServiceId,
                                                 @RequestParam List<Long> pdServiceVersionLinks,
                                                    지라이슈_단순_집계_요청 지라이슈_단순_검색_요청) {
        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("pdServiceId",pdServiceId)
                        ,new TermsQueryFilter("pdServiceVersions",pdServiceVersionLinks)
                );

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(일반_집계_요청.of(지라이슈_단순_검색_요청, esQuery)));
    }

    @GetMapping("/normal-versionAndMail-filter/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일반_버전_및_작업자_필터_검색(@PathVariable Long pdServiceId,
                                                      @RequestParam List<Long> pdServiceVersionLinks,
                                                      @RequestParam List<String> mailAddressList,
                                                      지라이슈_단순_집계_요청 지라이슈_단순_검색_요청) {
        EsQuery esQuery
                = new EsQueryBuilder()
                .bool(  new TermsQueryFilter("assignee.assignee_emailAddress.keyword", mailAddressList),
                        new TermsQueryFilter("pdServiceVersions",pdServiceVersionLinks),
                        new TermQueryMust("pdServiceId",pdServiceId)
                );

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(일반_집계_요청.of(지라이슈_단순_검색_요청, esQuery)));
    }

    @GetMapping("/assignees-requirements-involvements")
    public ResponseEntity<List<Worker>> 작업자_별_요구사항_별_관여도_apache(
            지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청
    ) {
        return ResponseEntity.ok(지라이슈_검색엔진.작업자_별_요구사항_별_관여도(지라이슈_제품_및_제품버전_집계_요청));
    }

    @PostMapping("/req-status-and-reqInvolved-unique-assignees")
    public ResponseEntity<List<제품_서비스_버전>>
        요구사항_별_상태_및_관여_작업자_수(@RequestBody 지라이슈_제품_및_제품버전_병합_집계_요청 병합집계요청) {

        log.info("[엘라스틱_지라이슈_대시보드_컨트롤러 :: 요구사항_별_상태_및_관여_작업자_수 ] :: 병합_요청_사항_요청값_생성 -> {}", 병합집계요청.get요구_사항());
        검색결과_목록_메인 요구사항
            =  지라이슈_검색엔진.집계결과_가져오기(일반_집계_요청.of(병합집계요청.get요구_사항(), 병합_요청_사항_요청값_생성(병합집계요청.get요구_사항())));

        검색결과_목록_메인 하위_이슈_사항
            =  지라이슈_검색엔진.집계결과_가져오기(일반_집계_요청.of(병합집계요청.get요구_사항(), 병합_요청_사항_요청값_생성(병합집계요청.get하위_이슈_사항())));

        return ResponseEntity.ok(지라이슈_검색엔진.요구사항_별_상태_및_관여_작업자수_내용(요구사항,하위_이슈_사항));
    }

    @GetMapping("/exclusion-isreq-normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 요구사항여부제외_일반_검색(@PathVariable Long pdServiceId, 지라이슈_일반_집계_요청 지라이슈_일반_집계_요청) {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId",pdServiceId));

        검색결과_목록_메인 집계결과_가져오기 = 지라이슈_검색엔진.집계결과_가져오기(일반_집계_요청.of(지라이슈_일반_집계_요청, esQuery));
        return ResponseEntity.ok(집계결과_가져오기);
    }

    @GetMapping("/isreq-normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 요구사항_일반_검색(@PathVariable Long pdServiceId, 지라이슈_일반_집계_요청 지라이슈_일반_집계_요청) {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId",pdServiceId));

        검색결과_목록_메인 집계결과_가져오기 = 지라이슈_검색엔진.집계결과_가져오기(크기별_집계_요청.of(지라이슈_일반_집계_요청, esQuery));
        return ResponseEntity.ok(집계결과_가져오기);
    }

    @GetMapping("/standard-daily/jira-issue")
    public ResponseEntity<Map<String, 일자별_요구사항_연결된이슈_생성개수_및_상태데이터>>
                    기준일자별_제품_및_제품버전목록_요구사항_및_연결된이슈_집계(
                            지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청){

        return ResponseEntity.ok(지라이슈_검색엔진.지라이슈_기준일자별_제품_및_제품버전_집계검색(지라이슈_일자별_제품_및_제품버전_집계_요청));
    }

    @GetMapping("/standard-daily/updated-jira-issue")
    public ResponseEntity<List<지라이슈>>
    기준일자별_제품_및_제품버전목록_업데이트된_이슈조회(
            지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청){

        return ResponseEntity.ok(지라이슈_검색엔진.지라이슈_기준일자별_제품_및_제품버전_업데이트된_이슈조회(지라이슈_일자별_제품_및_제품버전_집계_요청));
    }
    @ResponseBody
    @GetMapping("/standard-daily/updated-ridgeline")
    public Map<Long, Map<String, Map<String,List<요구사항_별_업데이트_데이터>>>>  기준일자별_제품_및_제품버전목록_업데이트된_누적_이슈조회(
            지라이슈_일자별_제품_및_제품버전_집계_요청 지라이슈_일자별_제품_및_제품버전_집계_요청) {

        return 지라이슈_검색엔진.요구사항별_업데이트_능선_데이터(지라이슈_일자별_제품_및_제품버전_집계_요청);
    }
    @GetMapping("/normal-version/resolution")
    public ResponseEntity<검색결과_목록_메인> 일반_버전필터_해결책유무_검색(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청,
                                                       @RequestParam(required = false) String resolution) {

        Boolean isReq = Optional.ofNullable(지라이슈_제품_및_제품버전_집계_요청.getIsReqType())
                .map(IsReqType::name)
                .map(name -> name.equals(IsReqType.REQUIREMENT.name()) ? Boolean.TRUE
                        : name.equals(IsReqType.ISSUE.name()) ? Boolean.FALSE
                        : null)
                .orElse(null);

        EsQuery esQuery
                = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("pdServiceId", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceLink())
                        , new TermQueryMust("isReq", isReq)
                        , new TermsQueryFilter("pdServiceVersions", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceVersionLinks())
                        , new ExistsQueryFilter(resolution)
                );

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(일반_집계_요청.of(지라이슈_제품_및_제품버전_집계_요청, esQuery)));
    }


    @PostMapping("/requirement-linkedissue/{pdServiceId}")
    public ResponseEntity<List<지라이슈>> 제품별_요구사항_연결이슈_조회(@PathVariable Long pdServiceId, 지라이슈_일반_검색_요청 지라이슈_일반_검색_요청) {

        EsQuery esQuery
                = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("pdServiceId",pdServiceId),
                        new TermsQueryFilter("pdServiceVersions",지라이슈_일반_검색_요청.getPdServiceVersionLinks())
                );

        return ResponseEntity.ok(지라이슈_검색엔진.지라이슈_조회(일반_검색_요청.of(지라이슈_일반_검색_요청, esQuery)));
    }

    private EsQuery 병합_요청_사항_요청값_생성(지라이슈_제품_및_제품버전_집계_요청 지라이슈_제품_및_제품버전_집계_요청){
        EsQuery esQuery
                = new EsQueryBuilder()
                .bool( new TermsQueryFilter("pdServiceVersions",지라이슈_제품_및_제품버전_집계_요청.getPdServiceVersionLinks()),
                        new TermQueryMust("pdServiceId",지라이슈_제품_및_제품버전_집계_요청.getPdServiceLink()),
                        new TermQueryMust("isReq",지라이슈_제품_및_제품버전_집계_요청.getIsReqType().isNotAllAndIsReq())
                );

        return esQuery;
    }
}
