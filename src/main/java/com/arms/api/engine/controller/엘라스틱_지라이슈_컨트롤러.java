package com.arms.api.engine.controller;

import com.arms.api.engine.model.지라이슈;
import com.arms.api.engine.service.지라이슈_서비스;
import com.arms.api.engine.vo.히트맵데이터;
import com.arms.api.jira.jiraissue.service.지라이슈_전략_호출;
import com.arms.elasticsearch.util.검색조건;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/engine/jira/{connectId}/issue")
@Slf4j
public class 엘라스틱_지라이슈_컨트롤러 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라이슈_서비스 지라이슈_검색엔진;

    @Autowired
    지라이슈_전략_호출 지라이슈_전략_호출;

    @ResponseBody
    @GetMapping("/get/{reqProjectKey}/{reqIssueKey}")
    public 지라이슈 요구사항이슈_조회(@PathVariable("connectId") Long 지라서버_아이디,
                              @PathVariable("reqProjectKey") String 지라프로젝트_키,
                              @PathVariable("reqIssueKey") String 지라이슈_키) {

        String 조회조건_아이디 = Long.toString(지라서버_아이디) + "_" + 지라프로젝트_키 + "_" + 지라이슈_키;

        log.info("조회조건_아이디 = " + 조회조건_아이디);

        return 지라이슈_검색엔진.이슈_조회하기(조회조건_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/search"},
            method = {RequestMethod.POST}
    )
    public List<지라이슈> 요구사항이슈_검색(@RequestBody final 검색조건 검색조건) {
        return 지라이슈_검색엔진.이슈_검색하기(검색조건);
    }


    /**
     * 2024-03-17 사용 여부 체크 필요
     */
    @ResponseBody
    @RequestMapping(
            value = {"/loadToES/{issueKey}"},
            method = {RequestMethod.GET}
    )
    public 지라이슈 이슈_검색엔진_저장(@PathVariable("connectId") Long 지라서버_아이디,
                               @PathVariable("issueKey") String 이슈_키,
                               @RequestParam("pdServiceId") Long 제품서비스_아이디,
                               @RequestParam("pdServiceVersions") Long[] 제품서비스_버전_아이디들,
                               @RequestParam("cReqLink") Long cReqLink,
                           ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("지라 이슈_검색엔진_저장");

        return 지라이슈_검색엔진.이슈_검색엔진_저장(지라서버_아이디, 이슈_키, 제품서비스_아이디, 제품서비스_버전_아이디들, cReqLink);
    }

    @ResponseBody
    @RequestMapping(
    value = {"/index/backup"},
    method = {RequestMethod.POST}
    )
    public boolean 지라이슈_인덱스백업() {
        로그.info("지라이슈_인덱스백업 컨트롤러");

        return 지라이슈_검색엔진.지라이슈_인덱스백업();
    }

    @ResponseBody
    @RequestMapping(
            value = {"/index"},
            method = {RequestMethod.DELETE}
    )
    public boolean 지라이슈_인덱스삭제() {
        로그.info("지라이슈_인덱스삭제 컨트롤러");

        return 지라이슈_검색엔진.지라이슈_인덱스삭제();
    }

    @ResponseBody
    @RequestMapping(
            value = {"/loadToES/bulk/{issueKey}"},
            method = {RequestMethod.GET}
    )
    public int 이슈_검색엔진_벌크_저장(@PathVariable("connectId") Long 지라서버_아이디,
                                       @PathVariable("issueKey") String 이슈_키,
                                       @RequestParam("pdServiceId") Long 제품서비스_아이디,
                                       @RequestParam("pdServiceVersions") Long[] 제품서비스_버전_아이디들,
                                       @RequestParam("cReqLink") Long cReqLink,
                                       ModelMap model, HttpServletRequest request) throws Exception {

        // 로그.info(" [ " + this.getClass().getName() + " :: 이슈_검색엔진_벌크_저장 ]" );
        로그.info("지라서버 아이디 : {}\n이슈 키 : {}\n제품서비스 아이디 : {}\n제품서비스 버전 목록 : {}"
                , 지라서버_아이디, 이슈_키, 제품서비스_아이디, 제품서비스_버전_아이디들);

        return 지라이슈_검색엔진.이슈_링크드이슈_서브테스크_벌크로_추가하기(지라서버_아이디, 이슈_키, 제품서비스_아이디, 제품서비스_버전_아이디들, cReqLink);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/increment/loadToES/bulk/{issueKey}"},
            method = {RequestMethod.GET}
    )
    public int 증분이슈_링크드이슈_서브테스크_벌크추가(@PathVariable("connectId") Long 지라서버_아이디,
                             @PathVariable("issueKey") String 이슈_키,
                             @RequestParam("pdServiceId") Long 제품서비스_아이디,
                             @RequestParam("pdServiceVersions") Long[] 제품서비스_버전_아이디들,
                             @RequestParam("cReqLink") Long cReqLink,
                             ModelMap model, HttpServletRequest request) throws Exception {

        로그.info("지라서버 아이디 : {},\t이슈 키 : {},\t제품서비스 아이디 : {},\t제품서비스 버전 목록 : {}"
                            , 지라서버_아이디, 이슈_키, 제품서비스_아이디, 제품서비스_버전_아이디들);

        return 지라이슈_검색엔진.증분이슈_링크드이슈_서브테스크_벌크추가(지라서버_아이디, 이슈_키, 제품서비스_아이디, 제품서비스_버전_아이디들, cReqLink);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/search/{issueKey}/subAndLinks"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈> 요구사항_링크드이슈_서브테스크_검색하기(@PathVariable("connectId") Long 지라서버_아이디,
                                                @PathVariable("issueKey") String 이슈_키,
                                                @RequestParam("page") int 페이지_번호,
                                                @RequestParam("size") int 페이지_사이즈 ) {

        return 지라이슈_검색엔진.요구사항_링크드이슈_서브테스크_검색하기(지라서버_아이디, 이슈_키, 페이지_번호, 페이지_사이즈);
    }

    /* 통합으로 변경가능 API */
    @ResponseBody
    @GetMapping("/getProgress/{pdServiceId}")
    public Map<String, Long> 제품서비스_버전별_상태값_통계(@PathVariable("connectId") Long 지라서버_아이디,
                                       @PathVariable("pdServiceId") Long 제품서비스_아이디,
                                       @RequestParam("pdServiceVersions") Long[] 제품서비스_버전_아이디) throws IOException {

        return 지라이슈_검색엔진.제품서비스_버전별_상태값_통계(제품서비스_아이디,제품서비스_버전_아이디);
    }

    @ResponseBody
    @GetMapping("/getReqCount/{pdServiceId}")
    public Map<String, Long> 제품서비스별_담당자_요구사항_통계(@PathVariable("connectId") Long 지라서버_아이디,
                                                @PathVariable("pdServiceId") Long 제품서비스_아이디,
                                                @RequestParam("assigneeEmail") String 담당자_이메일) throws IOException {

        로그.info("제품서비스별 요구사항 개수");

        return 지라이슈_검색엔진.제품서비스별_담당자_요구사항_통계(지라서버_아이디, 제품서비스_아이디, 담당자_이메일);
    }

    @ResponseBody
    @GetMapping("/getReqCount/{pdServiceId}/{issueKey}")
    public Map<String, Long> 제품서비스별_담당자_연관된_요구사항_통계(@PathVariable("connectId") Long 지라서버_아이디,
                                                    @PathVariable("pdServiceId") Long 제품서비스_아이디,
                                                    @PathVariable("issueKey") String 이슈키,
                                                    @RequestParam("assigneeEmail") String 담당자_이메일) throws IOException {

        로그.info("제품서비스별 연관된 요구사항 개수");

        return 지라이슈_검색엔진.제품서비스별_담당자_연관된_요구사항_통계(지라서버_아이디, 제품서비스_아이디, 이슈키, 담당자_이메일);
    }

    @ResponseBody
    @GetMapping("/pdService/pdServiceVersions")
    public List<지라이슈> 제품서비스_버전목록으로_조회(@RequestParam Long pdServiceLink,
                                    @RequestParam Long[] pdServiceVersionLinks) throws IOException {

        로그.info("제품서비스_버전목록으로_조회");

        List<지라이슈> 제품서비스_버전목록으로_조회 = 지라이슈_검색엔진.제품서비스_버전목록으로_조회(pdServiceLink, pdServiceVersionLinks);
        return 제품서비스_버전목록으로_조회;
    }

    @ResponseBody
    @GetMapping("/pdService/pdServiceVersions/heatmap")
    public 히트맵데이터 히트맵_제품서비스_버전목록으로_조회(@RequestParam Long pdServiceLink,
                                      @RequestParam Long[] pdServiceVersionLinks) {
        로그.info("히트맵_제품서비스_버전목록으로_조회");

        return 지라이슈_검색엔진.히트맵_제품서비스_버전목록으로_조회(pdServiceLink, pdServiceVersionLinks);
    }


    /*
     * 상태값 전체 통계
     * */
/*  사용하지 않는 Endpoint정리
    @ResponseBody
    @GetMapping("/search/req/status")
    public Map<String,Integer> 상태값_조회(@PathVariable("connectId") Long 지라서버_아이디) throws IOException {
        로그.info("전체 상태값 통계");
        return 지라이슈_검색엔진.요구사항_릴레이션이슈_상태값_전체통계(지라서버_아이디);
    }
*/

    /*
     * 프로젝트별 상태값 전체 통계
     * */
/*  사용하지 않는 Endpoint정리
    @ResponseBody
    @GetMapping("/search/req/status/project")
    public Map<String, Map<String, Integer>>프로젝트별_상태값_조회(@PathVariable("connectId") Long 지라서버_아이디) throws IOException {
        로그.info("프로젝트별 상태값 통계");
        return 지라이슈_검색엔진.요구사항_릴레이션이슈_상태값_프로젝트별통계(지라서버_아이디);
    }
*/

    /*
     *  하위 이슈
     * */
/*  사용하지 않는 Endpoint정리
    @ResponseBody
    @GetMapping("/getAssignee/{pdServiceId}")
    public Map<String, Long> 제품서비스별_담당자_통계(@PathVariable("connectId") Long 지라서버_아이디,
                                                @PathVariable("pdServiceId") Long 제품서비스_아이디) throws IOException {

        return 지라이슈_검색엔진.제품서비스별_담당자_통계(지라서버_아이디, 제품서비스_아이디);
    }
*/

/* 사용하지 않는 Endpoint정리
    @ResponseBody
    @GetMapping("/getDateDiff/{pdServiceId}")
    public Map<String, Long> 제품서비스별_소요일_통계(@PathVariable("connectId") Long 지라서버_아이디,
                                                @PathVariable("pdServiceId") Long 제품서비스_아이디)
                                                 throws IOException {
        로그.info("요구사항_제품별_소요일_빈도수_통계");

        return 지라이슈_검색엔진.제품서비스별_소요일_통계(지라서버_아이디, 제품서비스_아이디);
    }
*/
}
