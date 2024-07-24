package com.arms.api.alm.issue.base.controller;

import com.arms.api.alm.issue.base.model.dto.지라이슈_데이터;
import com.arms.api.alm.issue.base.model.dto.지라이슈생성_데이터;
import com.arms.api.alm.issue.base.service.이슈전략_호출;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/{connectId}/jira/issue")
public class 이슈_컨트롤러 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 이슈전략_호출 이슈전략_호출;

    @ResponseBody
    @RequestMapping(
            value = {"/list/{projectKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈_데이터> 이슈_목록_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                     @PathVariable("projectKeyOrId") String 프로젝트_키_또는_아이디) {

        로그.info("이슈_목록_가져오기 :: 연결_아이디 :: {} :: 프로젝트_키_또는_아이디 :: {}",
                                                연결_아이디, 프로젝트_키_또는_아이디);
        return 이슈전략_호출.이슈_목록_가져오기(연결_아이디, 프로젝트_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {""},
            method = {RequestMethod.POST}
    )
    public 지라이슈_데이터 이슈_생성하기(@PathVariable("connectId") Long 연결_아이디,
                                            @RequestBody 지라이슈생성_데이터 지라이슈생성_데이터) {

        로그.info("이슈_생성하기 :: 연결_아이디 :: {} :: 지라이슈생성_데이터 :: {}",
                                            연결_아이디, 지라이슈생성_데이터.toString());
        return 이슈전략_호출.이슈_생성하기(연결_아이디, 지라이슈생성_데이터);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.PUT}
    )
    public Map<String,Object> 이슈_수정하기(@PathVariable("connectId") Long 연결_아이디,
                                      @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디,
                                      @RequestBody 지라이슈생성_데이터 지라이슈생성_데이터) {

        로그.info("이슈_수정하기 :: 연결_아이디 :: {} :: 지라이슈생성_데이터 :: {}",
                                            연결_아이디, 지라이슈생성_데이터.toString());
        return 이슈전략_호출.이슈_수정하기(연결_아이디, 이슈_키_또는_아이디, 지라이슈생성_데이터);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}/{statusId}"},
            method = {RequestMethod.PUT, RequestMethod.POST}
    )
    public Map<String,Object> 이슈_상태_변경하기(@PathVariable("connectId") Long 연결_아이디,
                                      @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디,
                                      @PathVariable("statusId") String 상태_아이디) {

        로그.info("이슈_상태_변경하기 :: 연결_아이디 :: {} :: 이슈_키_또는_아이디 :: {} :: 상태_아이디 :: {}",
                연결_아이디, 이슈_키_또는_아이디, 상태_아이디);
        return 이슈전략_호출.이슈_상태_변경하기(연결_아이디, 이슈_키_또는_아이디, 상태_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.DELETE}
    )
    public Map<String,Object> 이슈_삭제하기(@PathVariable("connectId") Long 연결_아이디,
                                            @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디) {

        로그.info("이슈_삭제하기 :: 연결_아이디 :: {} :: 이슈_키_또는_아이디 :: {}",
                                            연결_아이디, 이슈_키_또는_아이디);
        return 이슈전략_호출.이슈_삭제하기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public 지라이슈_데이터 이슈_상세정보_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                 @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디) {

        로그.info("이슈_상세정보_가져오기 :: 연결_아이디 : {}, 이슈_키_또는_아이디 : {}",
                                                    연결_아이디, 이슈_키_또는_아이디);
        return 이슈전략_호출.이슈_상세정보_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/link/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈_데이터> 이슈링크_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                    @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디) {

        로그.info("이슈링크_가져오기 :: 연결_아이디 : {}, 이슈_키_또는_아이디 : {}",
                                                    연결_아이디, 이슈_키_또는_아이디);
        return 이슈전략_호출.이슈링크_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/subtask/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈_데이터> 서브테스크_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                           @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디) {

        로그.info("서브테스크_가져오기 :: 연결_아이디 : {}, 이슈_키_또는_아이디 : {}",
                                                연결_아이디, 이슈_키_또는_아이디);
        return 이슈전략_호출.서브테스크_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/increment/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public 지라이슈_데이터 증분이슈_상세정보_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                 @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디) {

        로그.info("증분이슈_상세정보_가져오기 :: 연결_아이디 : {}, 이슈_키_또는_아이디 : {}",
                                                    연결_아이디, 이슈_키_또는_아이디);
        return 이슈전략_호출.증분이슈_상세정보_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/increment/link/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈_데이터> 증분이슈링크_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                    @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디) {

        로그.info("증분이슈링크_가져오기 :: 연결_아이디 : {}, 이슈_키_또는_아이디 : {}",
                                                연결_아이디, 이슈_키_또는_아이디);
        return 이슈전략_호출.증분이슈링크_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/increment/subtask/{issueKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈_데이터> 증분서브테스크_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                     @PathVariable("issueKeyOrId") String 이슈_키_또는_아이디) {

        로그.info("증분서브테스크_가져오기 :: 연결_아이디 : {}, 이슈_키_또는_아이디 : {}",
                                                연결_아이디, 이슈_키_또는_아이디);
        return 이슈전략_호출.증분서브테스크_가져오기(연결_아이디, 이슈_키_또는_아이디);
    }
}
