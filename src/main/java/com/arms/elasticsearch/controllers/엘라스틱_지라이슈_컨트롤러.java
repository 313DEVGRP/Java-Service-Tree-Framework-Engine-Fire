package com.arms.elasticsearch.controllers;

import com.arms.elasticsearch.helper.인덱스자료;
import com.arms.elasticsearch.models.지라이슈;
import com.arms.elasticsearch.services.지라이슈_서비스;
import com.arms.elasticsearch.util.검색결과;
import com.arms.elasticsearch.util.검색조건;
import com.arms.jira.jiraissue.model.지라이슈_데이터;
import com.arms.jira.jiraissue.service.지라이슈_전략_호출;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
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
    @RequestMapping(
            value = {"/add"},
            method = {RequestMethod.POST}
    )
    public 지라이슈 요구사항이슈_추가(@PathVariable("connectId") Long 지라서버_아이디,
                          ModelMap model, HttpServletRequest request) throws Exception {

        지라이슈.프로젝트 더미프로젝트 = 지라이슈.프로젝트.builder()
                .id("더미테스트프로젝트")
                .key("PROJECT-KEY")
                .name("프로젝트이름")
                .self("http://www.a-rms.net")
                .build();

        지라이슈 더미이슈 = 지라이슈.builder()
                .jira_server_id(지라서버_아이디)
                .self("http://www.313.co.kr")
                .key("ISSUE-KEY")
                .project(더미프로젝트)
                .build();

        더미이슈.generateId();

        return 지라이슈_검색엔진.이슈_추가하기(더미이슈);
    }

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
                                        ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("지라 이슈_검색엔진_저장");

        지라이슈_데이터 받아온_이슈 = 지라이슈_전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키);

        지라이슈.프로젝트 프로젝트 = 지라이슈.프로젝트.builder()
                .id(받아온_이슈.getFields().getProject().getId())
                .key(받아온_이슈.getFields().getProject().getKey())
                .name(받아온_이슈.getFields().getProject().getName())
                .self(받아온_이슈.getFields().getProject().getSelf())
                .build();

        지라이슈 이슈 = 지라이슈.builder()
                .jira_server_id(지라서버_아이디)
                .self(받아온_이슈.getSelf())
                .key(받아온_이슈.getKey())
                .issueID(받아온_이슈.getId().toString())
                .project(프로젝트)
                .parentReqKey("iAmParent")
                .isReq(true)
                .build();

        이슈.generateId();

        return 지라이슈_검색엔진.이슈_추가하기(이슈);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/loadToES/bulk/{issueKey}"},
            method = {RequestMethod.GET}
    )
    public int 이슈_검색엔진_벌크_저장(@PathVariable("connectId") Long 지라서버_아이디,
                                       @PathVariable("issueKey") String 이슈_키,
                                       ModelMap model, HttpServletRequest request) throws Exception {

        로그.info("지라 이슈_검색엔진_벌크_저장 컨트롤러");

        return 지라이슈_검색엔진.이슈_링크드이슈_서브테스크_벌크로_추가하기(지라서버_아이디, 이슈_키);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/loadToEStest/{issueKey}"},
            method = {RequestMethod.GET}
    )
    public int 이슈_검색엔진_저장(@PathVariable("connectId") Long 지라서버_아이디,
                          @PathVariable("issueKey") String 이슈_키) throws Exception {
        로그.info("지라 이슈_상세정보_가져오기");

        지라이슈_데이터 받아온_이슈 = 지라이슈_전략_호출.이슈_상세정보_가져오기(지라서버_아이디, 이슈_키);
        List<지라이슈_데이터> 받아온_이슈링크_목록 = 지라이슈_전략_호출.이슈링크_가져오기(지라서버_아이디, 이슈_키);
        List<지라이슈_데이터> 받아온_서브테스크_목록 = 지라이슈_전략_호출.서브테스크_가져오기(지라서버_아이디, 이슈_키);
        List<지라이슈> 저장할_리스트 = new ArrayList<>();

        지라이슈.프로젝트 프로젝트 = 프로젝트_정보(받아온_이슈);

        지라이슈 이슈 = 지라이슈.builder()
                .jira_server_id(지라서버_아이디)
                .self(받아온_이슈.getSelf())
                .key(받아온_이슈.getKey())
                .issueID(받아온_이슈.getId().toString())
                .project(프로젝트)
                .parentReqKey("iAmParent")
                .isReq(true)
                .build();

        이슈.generateId();
        저장할_리스트.add(이슈);

        for (지라이슈_데이터 이슈링크 : 받아온_이슈링크_목록) {
            지라이슈 이슈링크_이슈 = 지라이슈.builder()
                    .jira_server_id(지라서버_아이디)
                    .self(이슈링크.getSelf())
                    .key(이슈링크.getId().toString())
                    .project(프로젝트_정보(이슈링크))
                    .parentReqKey(이슈_키)
                    .isReq(false)
                    .build();

            이슈링크_이슈.generateId();
            저장할_리스트.add(이슈링크_이슈);
        }

        for (지라이슈_데이터 서브테스크 : 받아온_서브테스크_목록) {
            지라이슈 이슈링크_이슈 = 지라이슈.builder()
                    .jira_server_id(지라서버_아이디)
                    .self(서브테스크.getSelf())
                    .key(서브테스크.getId().toString())
                    .project(프로젝트_정보(서브테스크))
                    .parentReqKey(이슈_키)
                    .isReq(false)
                    .build();

            이슈링크_이슈.generateId();
            저장할_리스트.add(이슈링크_이슈);
        }

        return 지라이슈_검색엔진.대량이슈_추가하기(저장할_리스트);
    }

    public 지라이슈.프로젝트 프로젝트_정보(지라이슈_데이터 받아온_이슈) {

        return 지라이슈.프로젝트.builder()
                .id(받아온_이슈.getFields().getProject().getId())
                .key(받아온_이슈.getFields().getProject().getKey())
                .name(받아온_이슈.getFields().getProject().getName())
                .self(받아온_이슈.getFields().getProject().getSelf())
                .build();
    }

    @ResponseBody
    @RequestMapping(
            value = {"/paging/{issueKey}/{pageNum}"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈> 페이징_이슈_조회하기(@PathVariable("connectId") Long 지라서버_아이디,
                                  @PathVariable("issueKey") String 이슈_키,
                                  @PathVariable("pageNum") int 페이지_번호) {

        return 지라이슈_검색엔진.페이징_이슈_조회하기(지라서버_아이디, 이슈_키, 페이지_번호);
    }
}
