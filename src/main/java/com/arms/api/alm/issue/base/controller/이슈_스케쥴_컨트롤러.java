package com.arms.api.alm.issue.base.controller;

import com.arms.api.alm.issue.base.model.지라이슈_엔티티;
import com.arms.api.alm.issue.base.service.이슈_스케쥴_서비스;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/engine/jira/{connectId}/issue")
@Slf4j
public class 이슈_스케쥴_컨트롤러 {


    @Autowired
    private 이슈_스케쥴_서비스 이슈_스케쥴_서비스;

    // 스케쥴러 컨트롤러
    @ResponseBody
    @GetMapping("/get/{reqProjectKey}/{reqIssueKey}")
    public 지라이슈_엔티티 요구사항이슈_조회(@PathVariable("connectId") Long 지라서버_아이디,
                              @PathVariable("reqProjectKey") String 지라프로젝트_키,
                              @PathVariable("reqIssueKey") String 지라이슈_키) {

        String 조회조건_아이디 = Long.toString(지라서버_아이디) + "_" + 지라프로젝트_키 + "_" + 지라이슈_키;

        log.info("조회조건_아이디 = " + 조회조건_아이디);

        return 이슈_스케쥴_서비스.이슈_조회하기(조회조건_아이디);
    }



    /**
     * 2024-03-17 사용 여부 체크 필요
     */
    @ResponseBody
    @RequestMapping(
            value = {"/loadToES/{issueKey}"},
            method = {RequestMethod.GET}
    )
    public 지라이슈_엔티티 이슈_검색엔진_저장(@PathVariable("connectId") Long 지라서버_아이디,
                               @PathVariable("issueKey") String 이슈_키,
                               @RequestParam("pdServiceId") Long 제품서비스_아이디,
                               @RequestParam("pdServiceVersions") Long[] 제품서비스_버전_아이디들,
                               @RequestParam("cReqLink") Long cReqLink,
                               ModelMap model, HttpServletRequest request) throws Exception {
        log.info("지라 이슈_검색엔진_저장");

        return 이슈_스케쥴_서비스.이슈_검색엔진_저장(지라서버_아이디, 이슈_키, 제품서비스_아이디, 제품서비스_버전_아이디들, cReqLink);
    }

    @ResponseBody
    @RequestMapping(
    value = {"/index/backup"},
    method = {RequestMethod.POST}
    )
    public boolean 지라이슈_인덱스백업() {
        log.info("지라이슈_인덱스백업 컨트롤러");

        return 이슈_스케쥴_서비스.지라이슈_인덱스백업();
    }

    @ResponseBody
    @RequestMapping(
            value = {"/index"},
            method = {RequestMethod.DELETE}
    )
    public boolean 지라이슈_인덱스삭제() {
        log.info("지라이슈_인덱스삭제 컨트롤러");

        return 이슈_스케쥴_서비스.지라이슈_인덱스삭제();
    }

    // 스케쥴러 컨트롤러
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
                                    @RequestParam("projectKeyOrId") String 프로젝트키_또는_아이디,
                              ModelMap model, HttpServletRequest request) throws Exception {

        log.info(" [ 이슈_검색엔진_벌크_저장 ] :: 지라서버 아이디 : {},\t이슈 키 : {},\t프로젝트키_또는_아이디 : {},\t제품서비스 아이디 : {},\t제품서비스 버전 목록 : {}"
                , 지라서버_아이디, 이슈_키, 프로젝트키_또는_아이디, 제품서비스_아이디, 제품서비스_버전_아이디들);

        return 이슈_스케쥴_서비스.이슈_링크드이슈_서브테스크_벌크로_추가하기(지라서버_아이디, 이슈_키, 프로젝트키_또는_아이디, 제품서비스_아이디, 제품서비스_버전_아이디들, cReqLink);
    }

    // 스케쥴러 컨트롤러
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
                                     @RequestParam("projectKeyOrId") String 프로젝트키_또는_아이디,
                             ModelMap model, HttpServletRequest request) throws Exception {

        log.info("지라서버 아이디 : {},\t이슈 키 : {},\t프로젝트키_또는_아이디 : {},\t제품서비스 아이디 : {},\t제품서비스 버전 목록 : {}"
                            , 지라서버_아이디, 이슈_키, 프로젝트키_또는_아이디, 제품서비스_아이디, 제품서비스_버전_아이디들);

        return 이슈_스케쥴_서비스.증분이슈_링크드이슈_서브테스크_벌크추가(지라서버_아이디, 이슈_키, 프로젝트키_또는_아이디, 제품서비스_아이디, 제품서비스_버전_아이디들, cReqLink);
    }

}
