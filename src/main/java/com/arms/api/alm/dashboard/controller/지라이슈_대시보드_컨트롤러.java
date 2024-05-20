package com.arms.api.alm.dashboard.controller;

import com.arms.api.util.model.dto.지라이슈_기본_집계_요청;
import com.arms.api.util.model.dto.트리맵_집계_요청;
import com.arms.api.util.model.vo.Worker;
import com.arms.elasticsearch.query.EsQuery;
import com.arms.elasticsearch.query.esquery.EsQueryBuilder;
import com.arms.elasticsearch.query.esquery.esboolquery.must.MustTermQuery;
import com.arms.elasticsearch.query.factory.creator.하위_계층_집계_쿼리_생성기;
import com.arms.elasticsearch.버킷_집계_결과_목록_합계;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/engine/jira/dashboard")
@Slf4j
public class 지라이슈_대시보드_컨트롤러 {

    @Autowired
    private com.arms.api.alm.dashboard.service.지라이슈_대시보드_서비스 지라이슈_대시보드_서비스;


    // Dashboard
    @PostMapping("/assignees-requirements-involvements")
    public ResponseEntity<List<Worker>> 작업자_별_요구사항_별_관여도_apache(
            @RequestBody 트리맵_집계_요청 트리맵_집계_요청
    ) {
        return ResponseEntity.ok(지라이슈_대시보드_서비스.작업자_별_요구사항_별_관여도(트리맵_집계_요청));
    }

    // Dashboard
    @GetMapping("/exclusion-isreq-normal/{pdServiceId}")
    public ResponseEntity<버킷_집계_결과_목록_합계> 요구사항여부제외_일반_검색(@PathVariable Long pdServiceId, 지라이슈_기본_집계_요청 지라이슈_일반_집계_요청) {

        EsQuery esQuery
                = new EsQueryBuilder()
                    .bool(new MustTermQuery("pdServiceId",pdServiceId));

        버킷_집계_결과_목록_합계 집계결과_가져오기 = 지라이슈_대시보드_서비스.집계결과_가져오기(하위_계층_집계_쿼리_생성기.of(지라이슈_일반_집계_요청, esQuery));
        return ResponseEntity.ok(집계결과_가져오기);
    }

    // Dashboard, Detail_Dashboard
    @GetMapping("/normal/{pdServiceId}")
    public ResponseEntity<버킷_집계_결과_목록_합계> 일반_검색(@PathVariable Long pdServiceId, 지라이슈_기본_집계_요청 지라이슈_일반_집계_요청) {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                         new MustTermQuery("pdServiceId",pdServiceId)
                        ,new MustTermQuery("isReq", 지라이슈_일반_집계_요청.getIsReq())
                );

        return ResponseEntity.ok(지라이슈_대시보드_서비스.집계결과_가져오기(하위_계층_집계_쿼리_생성기.of(지라이슈_일반_집계_요청, esQuery)));
    }

}
