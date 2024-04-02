package com.arms.api.alm.issue.base.controller;

import com.arms.api.alm.issue.base.model.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.지라이슈생성_데이터;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/{connectId}/jira/issue")
public class 이슈_컨트롤러 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    com.arms.api.alm.issue.base.service.이슈전략_호출 이슈전략_호출;

    @ResponseBody
    @RequestMapping(
            value = {"/list/{projectKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈_데이터> 이슈_목록_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                     @PathVariable("projectKeyOrId") String 프로젝트_키_또는_아이디,
                                     ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("지라 이슈_목록_가져오기");
        return 이슈전략_호출.이슈_목록_가져오기(연결_아이디, 프로젝트_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {""},
            method = {RequestMethod.POST}
    )
    public 지라이슈_데이터 이슈_생성하기(@PathVariable("connectId") Long 연결_아이디,
                                            @RequestBody 지라이슈생성_데이터 지라이슈생성_데이터) throws Exception {
        로그.info("지라 이슈_생성하기");

        return 이슈전략_호출.이슈_생성하기(연결_아이디, 지라이슈생성_데이터);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.PUT}
    )
    public Map<String,Object> 이슈_수정하기(@PathVariable("connectId") Long 연결_아이디,
                                      @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디,
                                      @RequestBody 지라이슈생성_데이터 지라이슈생성_데이터) throws Exception {
        로그.info("지라 이슈 수정하기");

        return 이슈전략_호출.이슈_수정하기(연결_아이디, 이슈_키_또는_아이디, 지라이슈생성_데이터);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.DELETE}
    )
    public Map<String,Object> 이슈_삭제_라벨_처리하기(@PathVariable("connectId") Long 연결_아이디,
                                            @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디,
                                            ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("지라 이슈 삭제 라벨 처리하기");

        return 이슈전략_호출.이슈_삭제_라벨_처리하기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public 지라이슈_데이터 이슈_상세정보_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                 @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디,
                                 ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("연결_아이디 : {}, 이슈_키_또는_아이디 : {}", 연결_아이디, 이슈_키_또는_아이디);

        return 이슈전략_호출.이슈_상세정보_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/link/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈_데이터> 이슈링크_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                    @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디,
                                    ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("연결_아이디 : {}, 이슈_키_또는_아이디 : {}", 연결_아이디, 이슈_키_또는_아이디);

        return 이슈전략_호출.이슈링크_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/subtask/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈_데이터> 서브테스크_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                           @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디,
                                           ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("연결_아이디 : {}, 이슈_키_또는_아이디 : {}", 연결_아이디, 이슈_키_또는_아이디);

        return 이슈전략_호출.서브테스크_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/increment/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public 지라이슈_데이터 증분이슈_상세정보_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                 @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디,
                                 ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("연결_아이디 : {}, 이슈_키_또는_아이디 : {}", 연결_아이디, 이슈_키_또는_아이디);

        return 이슈전략_호출.증분이슈_상세정보_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/increment/link/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈_데이터> 증분이슈링크_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                    @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디,
                                    ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("연결_아이디 : {}, 이슈_키_또는_아이디 : {}", 연결_아이디, 이슈_키_또는_아이디);

        return 이슈전략_호출.증분이슈링크_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/increment/subtask/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈_데이터> 증분서브테스크_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                     @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디,
                                     ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("연결_아이디 : {}, 이슈_키_또는_아이디 : {}", 연결_아이디, 이슈_키_또는_아이디);

        return 이슈전략_호출.증분서브테스크_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }
}
