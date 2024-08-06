package com.arms.api.alm.requirement.controller;


import com.arms.api.alm.requirement.service.요구사항_서비스;
import com.arms.api.alm.issue.base.model.dto.지라이슈_엔티티;
import com.arms.api.util.model.dto.request.지라이슈_기본_검색_집계_하위_요청;
import com.arms.api.util.model.dto.request.지라이슈_제품_및_제품버전_검색_집계_하위_요청;
import com.arms.egovframework.javaservice.esframework.model.vo.버킷_집계_결과;
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
    public ResponseEntity<List<지라이슈_엔티티>> 제품별_요구사항_연결이슈_조회(@PathVariable Long pdServiceId, 지라이슈_기본_검색_집계_하위_요청 지라이슈_일반_집계_요청) {
        return ResponseEntity.ok(요구사항_서비스.제품별_요구사항_연결이슈_조회(pdServiceId, 지라이슈_일반_집계_요청));
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
            지라이슈_제품_및_제품버전_검색_집계_하위_요청 지라이슈_제품_및_제품버전_집계_요청
    ) {
        return ResponseEntity.ok(요구사항_서비스.제품_요구사항별_담당자_목록(지라이슈_제품_및_제품버전_집계_요청));
    }

    // 현황관리 - 요구사항 상세 이슈 조회 모달에 사용
    @GetMapping("/reqIssueAndItsSubtasks")
    public ResponseEntity<List<지라이슈_엔티티>> 요구사항_하위이슈_연결이슈_조회(
            @RequestParam("pdServiceId") Long pdServiceLink,
            @RequestParam("pdServiceVersions") Long[] pdServiceVersionLinks,
            @RequestParam("jiraServerId") String jiraServerId, // ALM서버 아이디
            @RequestParam("issueKey") String issueKey) {

        return ResponseEntity.ok(요구사항_서비스.요구사항이슈_연결하위이슈_조회(pdServiceLink, pdServiceVersionLinks, jiraServerId, issueKey));

    }

    @GetMapping("/reqIssues-created-together")
    public ResponseEntity<List<지라이슈_엔티티>> 요구사항_묶음_조회(
            @RequestParam("pdServiceId") Long pdServiceLink,
            @RequestParam("pdServiceVersions") Long[] pdServiceVersionLinks,
            @RequestParam("cReqLink") Long 요구사항_아이디) {

        return ResponseEntity.ok(요구사항_서비스.함께_생성된_요구사항_이슈목록(pdServiceLink, pdServiceVersionLinks, 요구사항_아이디));

    }



    @GetMapping("/deletedIssueList")
    public ResponseEntity<List<지라이슈_엔티티>> 제품_버전별_삭제된_이슈조회(
            @RequestParam("pdServiceId") Long pdServiceLink,
            @RequestParam("pdServiceVersions") Long[] pdServiceVersionLinks) {

        return ResponseEntity.ok(요구사항_서비스.제품_버전별_삭제된_이슈조회(pdServiceLink, pdServiceVersionLinks));
    }
    @PutMapping("/deleteWithdrawal")
    public ResponseEntity<?> 이슈_삭제철회(@RequestBody List<지라이슈_엔티티>  삭제_철회대상_목록) throws Exception {
        return ResponseEntity.ok(요구사항_서비스.이슈_삭제철회(삭제_철회대상_목록));
    }
}

