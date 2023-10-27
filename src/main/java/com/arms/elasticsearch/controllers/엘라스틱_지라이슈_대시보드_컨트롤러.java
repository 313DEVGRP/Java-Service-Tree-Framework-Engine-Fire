package com.arms.elasticsearch.controllers;

import com.arms.elasticsearch.models.dashboard.bar.요구사항_지라이슈상태_주별_집계;
import com.arms.elasticsearch.models.dashboard.donut.집계_응답;
import com.arms.elasticsearch.models.dashboard.sankey.SankeyElasticSearchData;
import com.arms.elasticsearch.models.지라이슈_검색_일반_요청;
import com.arms.elasticsearch.models.지라이슈_검색_일자별_요청;
import com.arms.elasticsearch.services.dashboard.bar.BarChart;
import com.arms.elasticsearch.services.dashboard.donut.DonutChart;
import com.arms.elasticsearch.services.dashboard.sankey.SankeyChart;
import com.arms.elasticsearch.services.지라이슈_대시보드_서비스;
import com.arms.elasticsearch.util.검색결과_목록_메인;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    private DonutChart donutChart;
    @Autowired
    private BarChart barChart;

    @ResponseBody
    @RequestMapping(
            value = {"/jira-issue-statuses"},
            method = {RequestMethod.GET}
    )
    public List<집계_응답> 요구사항이슈집계(
            @RequestParam Long pdServiceLink,
            @RequestParam List<Long> pdServiceVersionLinks
    ) throws IOException {
        List<집계_응답> 요구사항이슈집계 = donutChart.이슈상태집계(pdServiceLink, pdServiceVersionLinks);
        return 요구사항이슈집계;
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
            @RequestParam List<Long> pdServiceVersionLinks
    ) throws IOException {
        Map<String, List<SankeyElasticSearchData>> sankeyElasticSearchData = sankeyChart.제품_버전별_담당자_목록(pdServiceLink, pdServiceVersionLinks);
        return sankeyElasticSearchData;
    }

    @ResponseBody
    @GetMapping("/date/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일자별_검색(@PathVariable Long pdServiceId, 지라이슈_검색_일자별_요청 지라이슈_검색_일자별_요청) throws IOException {
        지라이슈_검색_일자별_요청.set서비스아이디(pdServiceId);
        return ResponseEntity.ok(지라이슈_검색엔진.집계결과_가져오기(지라이슈_검색_일자별_요청));
    }

    @ResponseBody
    @GetMapping("/normal/{pdServiceId}")
    public ResponseEntity<검색결과_목록_메인> 일반_검색(@PathVariable Long pdServiceId, 지라이슈_검색_일반_요청 지라이슈_검색_일반_요청) throws IOException {
        지라이슈_검색_일반_요청.set서비스아이디(pdServiceId);
        검색결과_목록_메인 집계결과_가져오기 = 지라이슈_검색엔진.집계결과_가져오기(지라이슈_검색_일반_요청);
        return ResponseEntity.ok(집계결과_가져오기);
    }
}
