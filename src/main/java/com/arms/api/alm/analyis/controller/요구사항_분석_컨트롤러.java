package com.arms.api.alm.analyis.controller;

import com.arms.api.alm.issue.base.model.지라이슈_엔티티;
import com.arms.api.util.model.dto.*;
import com.arms.api.util.model.enums.IsReqType;
import com.arms.api.alm.analyis.service.요구사항_분석_서비스;
import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.esquery.EsBoolQuery;
import com.arms.elasticsearch.query.esquery.EsQueryBuilder;
import com.arms.elasticsearch.query.esquery.esboolquery.must.MustTermQuery;
import com.arms.elasticsearch.query.factory.creator.중첩_집계_쿼리_생성기;
import com.arms.elasticsearch.query.filter.ExistsQueryFilter;
import com.arms.elasticsearch.query.filter.TermsQueryFilter;
import com.arms.elasticsearch.버킷_집계_결과;
import com.arms.elasticsearch.버킷_집계_결과_목록_합계;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/engine/jira/dashboard")
@Slf4j
public class 요구사항_분석_컨트롤러 {

    @Autowired
    private 요구사항_분석_서비스 요구사항_분석_서비스;

    // 대시보드, 디테일_대시보드, 범위분석, 리소스분석,
    @GetMapping("/aggregation/flat")
    public ResponseEntity<버킷_집계_결과_목록_합계> flatAggregation(
            지라이슈_제품_및_제품버전_검색__집계_하위_요청 검색요청
    ) {
        EsBoolQuery[] esBoolQueries = Stream.of(
                new MustTermQuery("pdServiceId", 검색요청.getPdServiceLink()),
                new TermsQueryFilter("pdServiceVersions", 검색요청.getPdServiceVersionLinks()),
                검색요청.getIsReqType() == IsReqType.REQUIREMENT ? new MustTermQuery("isReq", true) : null,
                검색요청.getIsReqType() == IsReqType.ISSUE ? new MustTermQuery("isReq", false) : null
        ).filter(Objects::nonNull).toArray(EsBoolQuery[]::new);

        EsQuery esQuery = new EsQueryBuilder().bool(esBoolQueries);

        return ResponseEntity.ok(요구사항_분석_서비스.집계결과_가져오기(중첩_집계_쿼리_생성기.포괄(검색요청, esQuery)));
    }

    // 대시보드, 디테일_대시보드, 범위분석

    @GetMapping("/requirements-jira-issue-statuses")
    public ResponseEntity<Map<String, 요구사항_지라이슈상태_주별_집계>> 요구사항이슈월별집계(
            지라이슈_제품_및_제품버전_검색__집계_하위_요청 지라이슈_제품_및_제품버전_집계_요청
    ) {
        return ResponseEntity.ok(요구사항_분석_서비스.요구사항_지라이슈상태_주별_집계(지라이슈_제품_및_제품버전_집계_요청));
    }

    // Dashboard, Detail_Dashboard, Analysis Resource
    @GetMapping("/version-assignees")
    public ResponseEntity<List<버킷_집계_결과>> 제품별_버전_및_작업자(
            지라이슈_제품_및_제품버전_검색__집계_하위_요청 지라이슈_제품_및_제품버전_집계_요청
    ) {
        return ResponseEntity.ok(요구사항_분석_서비스.제품_버전별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청));
    }


    // Analysis Resource, Analysis Cost
    @GetMapping("/normal-version/{pdServiceId}")
    public ResponseEntity<버킷_집계_결과_목록_합계> 일반_버전필터_검색(@PathVariable Long pdServiceId,
                                                     @RequestParam List<Long> pdServiceVersionLinks,
                                                     지라이슈_기본_검색__집계_하위_요청 지라이슈_일반_집계_요청) {
        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                     new MustTermQuery("pdServiceId",pdServiceId)
                    ,new MustTermQuery("isReq", 지라이슈_일반_집계_요청.getIsReq())
                    ,new TermsQueryFilter("pdServiceVersions",pdServiceVersionLinks)
                );

        return ResponseEntity.ok(요구사항_분석_서비스.집계결과_가져오기(중첩_집계_쿼리_생성기.포괄(지라이슈_일반_집계_요청, esQuery)));
    }

    // Analysis Resource, Analysis Scope
    @GetMapping("/normal-version-only/{pdServiceId}")
    public ResponseEntity<버킷_집계_결과_목록_합계> 일반_버전필터_집계(@PathVariable Long pdServiceId,
                                                     @RequestParam List<Long> pdServiceVersionLinks,
                                                     지라이슈_기본_검색__집계_하위_요청 지라이슈_일반_집계_요청) {
        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                     new MustTermQuery("pdServiceId",pdServiceId)
                    ,new TermsQueryFilter("pdServiceVersions",pdServiceVersionLinks)
                );

        return ResponseEntity.ok(요구사항_분석_서비스.집계결과_가져오기(중첩_집계_쿼리_생성기.포괄(지라이슈_일반_집계_요청, esQuery)));
    }

    // Analysis Resource
    @GetMapping("/normal-versionAndMail-filter/{pdServiceId}")
    public ResponseEntity<버킷_집계_결과_목록_합계> 일반_버전_및_작업자_필터_검색(@PathVariable Long pdServiceId,
                                                            @RequestParam List<Long> pdServiceVersionLinks,
                                                            @RequestParam List<String> mailAddressList,
                                                            지라이슈_기본_검색__집계_하위_요청 지라이슈_일반_집계_요청) {
        EsQuery esQuery
                = new EsQueryBuilder()
                .bool(  new TermsQueryFilter("assignee.assignee_emailAddress.keyword", mailAddressList),
                        new TermsQueryFilter("pdServiceVersions",pdServiceVersionLinks),
                        new MustTermQuery("pdServiceId",pdServiceId)
                );

        return ResponseEntity.ok(요구사항_분석_서비스.집계결과_가져오기(중첩_집계_쿼리_생성기.포괄(지라이슈_일반_집계_요청, esQuery)));
    }


    // Analysis Scope
    @GetMapping("/req-status-and-reqInvolved-unique-assignees-per-version/{pdServiceId}")
    public ResponseEntity<List<요구사항_버전_이슈_키_상태_작업자수>> 버전배열_요구사항_별_상태_및_관여_작업자_수(@PathVariable Long pdServiceId,
                                                                                @RequestParam Long[] pdServiceVersionLinks) {

        List<요구사항_버전_이슈_키_상태_작업자수> 요구사항_버전배열_이슈키_작업자수_목록 = 요구사항_분석_서비스.버전별_요구사항_상태_및_관여_작업자수_내용(pdServiceId, pdServiceVersionLinks);
        return ResponseEntity.ok(요구사항_버전배열_이슈키_작업자수_목록);
    }

    // Analysis Time
    @GetMapping("/standard-daily/jira-issue")
    public ResponseEntity<Map<String, 일자별_요구사항_연결된이슈_생성개수_및_상태데이터>>
    기준일자별_제품_및_제품버전목록_요구사항_및_연결된이슈_집계(
            지라이슈_일자별_제품_및_제품버전_검색__집계_하위_요청 지라이슈_일자별_제품_및_제품버전_집계_요청){

        return ResponseEntity.ok(요구사항_분석_서비스.지라이슈_기준일자별_제품_및_제품버전_집계검색(지라이슈_일자별_제품_및_제품버전_집계_요청));
    }

    // Analysis Time
    @GetMapping("/standard-daily/updated-jira-issue")
    public ResponseEntity<List<지라이슈_엔티티>>
    기준일자별_제품_및_제품버전목록_업데이트된_이슈조회(
            지라이슈_일자별_제품_및_제품버전_검색__집계_하위_요청 지라이슈_일자별_제품_및_제품버전_집계_요청){

        return ResponseEntity.ok(요구사항_분석_서비스.지라이슈_기준일자별_제품_및_제품버전_업데이트된_이슈조회(지라이슈_일자별_제품_및_제품버전_집계_요청));
    }

    // Analysis Time
    @ResponseBody
    @GetMapping("/standard-daily/updated-ridgeline")
    public Map<Long, Map<String, Map<String,List<요구사항_별_업데이트_데이터>>>>  기준일자별_제품_및_제품버전목록_업데이트된_누적_이슈조회(
            지라이슈_일자별_제품_및_제품버전_검색__집계_하위_요청 지라이슈_일자별_제품_및_제품버전_집계_요청) {

        return 요구사항_분석_서비스.요구사항별_업데이트_능선_데이터(지라이슈_일자별_제품_및_제품버전_집계_요청);
    }

    // Analysis Time
    @GetMapping("/normal-version/resolution")
    public ResponseEntity<버킷_집계_결과_목록_합계> 일반_버전필터_해결책유무_검색(지라이슈_제품_및_제품버전_검색__집계_하위_요청 지라이슈_제품_및_제품버전_집계_요청,
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
                        new MustTermQuery("pdServiceId", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceLink())
                        , new MustTermQuery("isReq", isReq)
                        , new TermsQueryFilter("pdServiceVersions", 지라이슈_제품_및_제품버전_집계_요청.getPdServiceVersionLinks())
                        , new ExistsQueryFilter(resolution)
                );

        return ResponseEntity.ok(요구사항_분석_서비스.집계결과_가져오기(중첩_집계_쿼리_생성기.포괄(지라이슈_제품_및_제품버전_집계_요청, esQuery)));
    }

    // Analysis Cost
    @GetMapping("/version-req-assignees")
    public ResponseEntity<List<버킷_집계_결과>> 제품별_버전_및_요구사항별_작업자(
            지라이슈_제품_및_제품버전_검색__집계_하위_요청 지라이슈_제품_및_제품버전_집계_요청
    ) {
        return ResponseEntity.ok(요구사항_분석_서비스.제품_버전별_요구사항별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청));
    }

    // Analysis Cost
    @GetMapping("/req-updated-list")
    public ResponseEntity<Map<String,List<요구사항_지라이슈키별_업데이트_목록_데이터>>> 요구사항_지라이슈키별_업데이트_목록(@RequestParam List<String> issueList) {
        return ResponseEntity.ok(요구사항_분석_서비스.요구사항_지라이슈키별_업데이트_목록(issueList));
    }

    // Analysis Time, Analysis Scope
    @ResponseBody
    @GetMapping("/pdService/pdServiceVersions")
    public List<지라이슈_엔티티> 제품서비스_버전목록으로_조회(@RequestParam Long pdServiceLink,
                                          @RequestParam Long[] pdServiceVersionLinks) throws IOException {

        log.info("제품서비스_버전목록으로_조회");

        List<지라이슈_엔티티> 제품서비스_버전목록으로_조회 = 요구사항_분석_서비스.제품서비스_버전목록으로_조회(pdServiceLink, pdServiceVersionLinks);
        return 제품서비스_버전목록으로_조회;
    }

    // Analysis Time
    @ResponseBody
    @GetMapping("/pdService/pdServiceVersions/heatmap")
    public 히트맵데이터 히트맵_제품서비스_버전목록으로_조회(@RequestParam Long pdServiceLink,
                                      @RequestParam Long[] pdServiceVersionLinks) {
        log.info("히트맵_제품서비스_버전목록으로_조회");

        return 요구사항_분석_서비스.히트맵_제품서비스_버전목록으로_조회(pdServiceLink, pdServiceVersionLinks);
    }



}
