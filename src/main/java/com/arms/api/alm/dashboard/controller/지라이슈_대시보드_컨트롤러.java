package com.arms.api.alm.dashboard.controller;

import com.arms.api.util.model.dto.request.지라이슈_기본_검색_집계_하위_요청;
import com.arms.api.util.model.dto.request.트리맵_검색_집계_하위_요청;
import com.arms.api.util.model.vo.Worker;
import com.arms.egovframework.javaservice.esframework.EsQuery;
import com.arms.egovframework.javaservice.esframework.esquery.EsQueryBuilder;
import com.arms.egovframework.javaservice.esframework.filter.TermsQueryFilter;
import com.arms.egovframework.javaservice.esframework.must.TermQueryMust;
import com.arms.egovframework.javaservice.esframework.factory.creator.중첩_집계_쿼리_생성기;
import com.arms.egovframework.javaservice.esframework.model.vo.버킷_집계_결과_목록_합계;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.arms.api.alm.dashboard.service.지라이슈_대시보드_서비스;

import java.util.List;

@RestController
@RequestMapping("/engine/jira/dashboard")
@Slf4j
public class 지라이슈_대시보드_컨트롤러 {

    @Autowired
    private 지라이슈_대시보드_서비스 지라이슈_대시보드_서비스;


    // Dashboard
    @PostMapping("/assignees-requirements-involvements")
    public ResponseEntity<List<Worker>> 작업자_별_요구사항_별_관여도_apache(
            @RequestBody 트리맵_검색_집계_하위_요청 트리맵_집계_요청
    ) {
        return ResponseEntity.ok(지라이슈_대시보드_서비스.작업자_별_요구사항_별_관여도(트리맵_집계_요청));
    }

    // Dashboard
    @GetMapping("/exclusion-isreq-normal/{pdServiceId}")
    public ResponseEntity<버킷_집계_결과_목록_합계> 요구사항여부제외_일반_검색(@PathVariable Long pdServiceId, 지라이슈_기본_검색_집계_하위_요청 지라이슈_일반_집계_요청) {

        EsQuery esQuery
                = new EsQueryBuilder()
                    .bool(new TermQueryMust("pdServiceId",pdServiceId));

        버킷_집계_결과_목록_합계 집계결과_가져오기 = 지라이슈_대시보드_서비스.집계결과_가져오기(중첩_집계_쿼리_생성기.포괄(지라이슈_일반_집계_요청, esQuery));
        return ResponseEntity.ok(집계결과_가져오기);
    }

    // Dashboard, Detail_Dashboard
    @GetMapping("/normal/{pdServiceId}")
    public ResponseEntity<버킷_집계_결과_목록_합계> 일반_검색(@PathVariable Long pdServiceId, 지라이슈_기본_검색_집계_하위_요청 지라이슈_일반_집계_요청) {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                         new TermQueryMust("pdServiceId",pdServiceId)
                        ,new TermQueryMust("isReq", 지라이슈_일반_집계_요청.getIsReq())
                );

        return ResponseEntity.ok(지라이슈_대시보드_서비스.집계결과_가져오기(중첩_집계_쿼리_생성기.포괄(지라이슈_일반_집계_요청, esQuery)));
    }

    @GetMapping("/pdService-version-req/{pdServiceId}")
    public ResponseEntity<버킷_집계_결과_목록_합계> 제품_버전_요구사항_이슈_집계(@PathVariable Long pdServiceId,
                                                     @RequestParam List<Long> pdServiceVersionLinks,
                                                     지라이슈_기본_검색_집계_하위_요청 지라이슈_일반_집계_요청) {
        EsQuery esQuery
                = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("pdServiceId", pdServiceId),
                        new TermsQueryFilter("pdServiceVersions", pdServiceVersionLinks),
                        new TermQueryMust("isReq", 지라이슈_일반_집계_요청.getIsReq())
                );

        return ResponseEntity.ok(지라이슈_대시보드_서비스.집계결과_가져오기(중첩_집계_쿼리_생성기.포괄(지라이슈_일반_집계_요청, esQuery)));
    }
}
