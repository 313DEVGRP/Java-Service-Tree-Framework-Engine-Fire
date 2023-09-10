package com.arms.elasticsearch.controllers;

import com.arms.elasticsearch.helper.인덱스자료;
import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.services.지라이슈_서비스;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색조건;
import com.arms.jira.jiraissue.service.지라이슈_전략_호출;
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

    @ResponseBody
    @GetMapping("/test/{groupByField}")
    public Map<String, Long> 테스트_조회(@PathVariable("groupByField") String 조회조건_필드) throws IOException {

        return 지라이슈_검색엔진.특정필드의_값들을_그룹화하여_빈도수가져오기(인덱스자료.지라이슈_인덱스명, 조회조건_필드);
    }

    @ResponseBody
    @GetMapping("/test/{searchField}/{searchTerm}/{groupField}")
    public List<검색결과> 테스트2_조회(@PathVariable("searchField") String 특정필드, @PathVariable("searchTerm") String 특정필드검색어, @PathVariable("groupField") String 그룹할필드) throws IOException {

        return 지라이슈_검색엔진.특정필드_검색후_다른필드_그룹결과(인덱스자료.지라이슈_인덱스명, 특정필드, 특정필드검색어, 그룹할필드 );
    }

    @ResponseBody
    @RequestMapping(
            value = {"/loadToES/{issueKey}"},
            method = {RequestMethod.GET}
    )
    public 지라이슈 이슈_검색엔진_저장(@PathVariable("connectId") Long 지라서버_아이디,
                               @PathVariable("issueKey") String 이슈_키,
                               @RequestParam("pdServiceId") Long 제품서비스_아이디,
                               @RequestParam("pdServiceVersion") Long 제품서비스_버전_아이디,
                                        ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("지라 이슈_검색엔진_저장");

        return 지라이슈_검색엔진.이슈_검색엔진_저장(지라서버_아이디, 이슈_키, 제품서비스_아이디, 제품서비스_버전_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/loadToES/bulk/{issueKey}"},
            method = {RequestMethod.GET}
    )
    public int 이슈_검색엔진_벌크_저장(@PathVariable("connectId") Long 지라서버_아이디,
                                       @PathVariable("issueKey") String 이슈_키,
                                       @RequestParam("pdServiceId") Long 제품서비스_아이디,
                                        @RequestParam("pdServiceVersion") Long 제품서비스_버전_아이디,
                                       ModelMap model, HttpServletRequest request) throws Exception {

        로그.info("지라 이슈_검색엔진_벌크_저장 컨트롤러");

        return 지라이슈_검색엔진.이슈_링크드이슈_서브테스크_벌크로_추가하기(지라서버_아이디, 이슈_키, 제품서비스_아이디, 제품서비스_버전_아이디);
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

    @ResponseBody
    @GetMapping("/getProgress/{pdServiceId}/{pdServiceVersion}")
    public Map<String, Long> 제품서비스_버전별_상태값_통계(@PathVariable("connectId") Long 지라서버_아이디,
                                       @PathVariable("pdServiceId") Long 제품서비스_아이디,
                                       @PathVariable("pdServiceVersion") Long 제품서비스_버전_아이디) throws IOException {

        return 지라이슈_검색엔진.제품서비스_버전별_상태값_통계(제품서비스_아이디,제품서비스_버전_아이디);
    }

    /*
    * 상태값 전체 통계
    * */
    @ResponseBody
    @GetMapping("/search/req/status")
    public Map<String,Integer> 상태값_조회(@PathVariable("connectId") Long 지라서버_아이디) throws IOException {
        로그.info("전체 상태값 통계");
        return 지라이슈_검색엔진.요구사항_릴레이션이슈_상태값_전체통계(지라서버_아이디);
    }

    /*
     * 프로젝트별 상태값 전체 통계
     * */
    @ResponseBody
    @GetMapping("/search/req/status/project")
    public Map<String, Map<String, Integer>>프로젝트별_상태값_조회(@PathVariable("connectId") Long 지라서버_아이디) throws IOException {
        로그.info("프로젝트별 상태값 통계");
        return 지라이슈_검색엔진.요구사항_릴레이션이슈_상태값_프로젝트별통계(지라서버_아이디);
    }

    /*
    *  하위 이슈
    * */
    @ResponseBody
    @GetMapping("/getAssignee/{pdServiceId}")
    public Map<String, Long> 제품서비스별_담당자_통계(@PathVariable("connectId") Long 지라서버_아이디,
                                                @PathVariable("pdServiceId") Long 제품서비스_아이디) throws IOException {

        return 지라이슈_검색엔진.제품서비스별_담당자_통계(지라서버_아이디, 제품서비스_아이디);
    }

    @ResponseBody
    @GetMapping("/getDateDiff/{pdServiceId}")
    public Map<String, Long> 제품서비스별_소요일_통계(@PathVariable("connectId") Long 지라서버_아이디,
                                                @PathVariable("pdServiceId") Long 제품서비스_아이디)
                                                 throws IOException {
        로그.info("요구사항_제품별_소요일_빈도수_통계");

        return 지라이슈_검색엔진.제품서비스별_소요일_통계(지라서버_아이디, 제품서비스_아이디);
    }
}
