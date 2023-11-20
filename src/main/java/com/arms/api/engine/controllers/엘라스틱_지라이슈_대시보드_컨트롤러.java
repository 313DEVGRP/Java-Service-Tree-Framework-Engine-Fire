package com.arms.api.engine.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.arms.api.engine.models.지라이슈_제품_및_제품버전_검색요청;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.arms.api.engine.models.dashboard.bar.요구사항_지라이슈상태_주별_집계;
import com.arms.api.engine.models.dashboard.donut.집계_응답;
import com.arms.api.engine.models.dashboard.resource.AssigneeData;
import com.arms.api.engine.models.dashboard.sankey.SankeyElasticSearchData;
import com.arms.api.engine.models.dashboard.treemap.Worker;
import com.arms.api.engine.models.지라이슈_단순_검색요청;
import com.arms.api.engine.models.지라이슈_일반_검색요청;
import com.arms.api.engine.models.지라이슈_일자별_검색요청;
import com.arms.api.engine.services.dashboard.bar.BarChart;
import com.arms.api.engine.services.dashboard.sankey.SankeyChart;
import com.arms.api.engine.services.dashboard.treemap.TreeMapChart;
import com.arms.api.engine.services.지라이슈_대시보드_서비스;
import com.arms.elasticsearch.util.query.EsQueryBuilder;
import com.arms.elasticsearch.util.query.EsQuery;
import com.arms.elasticsearch.util.query.bool.TermQueryMust;
import com.arms.elasticsearch.util.query.bool.TermsQueryFilter;
import com.arms.elasticsearch.util.query.sort.SortBy;
import com.arms.elasticsearch.util.query.검색_일반_요청;
import com.arms.elasticsearch.util.query.검색_일자별_요청;
import com.arms.elasticsearch.util.query.검색_크기별_요청;
import com.arms.elasticsearch.util.검색결과_목록_메인;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/engine/jira/dashboard")
@Slf4j
public class 엘라스틱_지라이슈_대시보드_컨트롤러 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라이슈_대시보드_서비스 지라이슈_검색엔진;
    @Autowired
    private SankeyChart sankeyChart;
    @Autowired
    private BarChart barChart;
    @Autowired
    private TreeMapChart treeMapChart;

    @ResponseBody
    @RequestMapping(
            value = {"/jira-issue-statuses"},
            method = {RequestMethod.GET}
    )
    public ResponseEntity<검색결과_목록_메인> 요구사항이슈집계(지라이슈_제품_및_제품버전_검색요청 지라이슈_제품_및_제품버전_검색요청) throws IOException {
        EsQuery esQuery = new EsQueryBuilder()
                .bool(  new TermQueryMust("pdServiceId", 지라이슈_제품_및_제품버전_검색요청.getPdServiceLink()),
                        new TermsQueryFilter("pdServiceVersion", 지라이슈_제품_및_제품버전_검색요청.getPdServiceVersionLinks()));
        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(검색_일반_요청.of(지라이슈_제품_및_제품버전_검색요청, esQuery)));
    }

    @ResponseBody
    @RequestMapping(
            value = {"/requirements-jira-issue-statuses"},
            method = {RequestMethod.GET}
    )
    public Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항이슈월별집계(
            @RequestParam Long pdServiceLink,
            @RequestParam List<Long> pdServiceVersionLinks
    ) throws IOException {
        Map<String, 요구사항_지라이슈상태_주별_집계> 요구사항이슈주별집계 = barChart.요구사항_지라이슈상태_주별_집계(pdServiceLink, pdServiceVersionLinks);
        return 요구사항이슈주별집계;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/assignee-jira-issue-statuses"},
            method = {RequestMethod.GET}
    )
    public Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별집계(
            @RequestParam Long pdServiceLink) throws IOException {
        Map<String, Map<String, Map<String, Integer>>> 담당자_요구사항여부_상태별_집계결과 = 지라이슈_검색엔진.담당자_요구사항여부_상태별집계(pdServiceLink);
        return 담당자_요구사항여부_상태별_집계결과;
    }

    @ResponseBody
    @RequestMapping(
            value = {"/issue-assignee/{pdServiceId}"},
            method = {RequestMethod.GET}
    )
    public Map<String, Long> 제품서비스별_담당자_이름_통계(
            @PathVariable("pdServiceId") Long 제품서비스_아이디) throws Exception {
        return 지라이슈_검색엔진.제품서비스별_담당자_이름_통계(0L, 제품서비스_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/version-assignees"},
            method = {RequestMethod.GET}
    )
    public Map<String, List<SankeyElasticSearchData>> 제품별_버전_및_작업자(
            @RequestParam Long pdServiceLink,
            @RequestParam List<Long> pdServiceVersionLinks,
            @RequestParam int maxResults
    ) throws IOException {
        Map<String, List<SankeyElasticSearchData>> sankeyElasticSearchData = sankeyChart.제품_버전별_담당자_목록(pdServiceLink, pdServiceVersionLinks, maxResults);
        return sankeyElasticSearchData;
    }

    @ResponseBody
    @GetMapping("/date/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일자별_검색(@PathVariable Long pdServiceId, 지라이슈_일자별_검색요청 지라이슈_일자별_검색요청) throws IOException {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                      new TermsQueryFilter(지라이슈_일자별_검색요청.get필터필드(),지라이슈_일자별_검색요청.get필터필드검색어()),
                      new TermQueryMust("pdServiceId",pdServiceId));

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(검색_일자별_요청.of(지라이슈_일자별_검색요청, esQuery)));
    }

    @ResponseBody
    @GetMapping("/normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일반_검색(@PathVariable Long pdServiceId, 지라이슈_일반_검색요청 지라이슈_일반_검색요청) throws IOException {

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
                                                 지라이슈_일반_검색요청 지라이슈_일반_검색요청) throws IOException {
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
                                                    지라이슈_단순_검색요청 지라이슈_단순_검색_요청) throws IOException {
        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(
                        new TermQueryMust("pdServiceId",pdServiceId)
                        ,new TermsQueryFilter("pdServiceVersion",pdServiceVersionLinks)
                );

        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(검색_일반_요청.of(지라이슈_단순_검색_요청, esQuery)));
    }

    @ResponseBody
    @RequestMapping(
            value = {"/assignees-requirements-involvements"},
            method = {RequestMethod.GET}
    )
    public List<Worker> 작업자_별_요구사항_별_관여도(
            @RequestParam Long pdServiceLink,
            @RequestParam List<Long> pdServiceVersionLinks,
            @RequestParam int maxResults
    ) throws IOException {
        return treeMapChart.작업자_별_요구사항_별_관여도(pdServiceLink, pdServiceVersionLinks, maxResults);
    }

    @ResponseBody
    @GetMapping("/exclusion-isreq-normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 요구사항여부제외_일반_검색(@PathVariable Long pdServiceId, 지라이슈_일반_검색요청 지라이슈_일반_검색요청) throws IOException {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId",pdServiceId));

        검색결과_목록_메인 집계결과_가져오기 = 지라이슈_검색엔진.집계결과_가져오기(검색_일반_요청.of(지라이슈_일반_검색요청, esQuery));
        return ResponseEntity.ok(집계결과_가져오기);
    }

    @ResponseBody
    @GetMapping("/isreq-normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 요구사항_일반_검색(@PathVariable Long pdServiceId, 지라이슈_일반_검색요청 지라이슈_일반_검색요청) throws IOException {

        EsQuery esQuery
            = new EsQueryBuilder()
                .bool(new TermQueryMust("pdServiceId",pdServiceId));

        검색결과_목록_메인 집계결과_가져오기 = 지라이슈_검색엔진.집계결과_가져오기(검색_크기별_요청.of(지라이슈_일반_검색요청, esQuery));
        return ResponseEntity.ok(집계결과_가져오기);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/resources/tasks"},
            method = {RequestMethod.GET}
    )
    public List<AssigneeData> 리소스_담당자_데이터_리스트(
            @RequestParam Long pdServiceLink,
            @RequestParam List<Long> pdServiceVersionLinks
    ) throws IOException {
        return 지라이슈_검색엔진.리소스_담당자_데이터_리스트(pdServiceLink, pdServiceVersionLinks);
    }
}
