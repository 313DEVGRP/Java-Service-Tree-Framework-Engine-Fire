package com.arms.api.alm.issue.status.controller;

import com.arms.api.alm.issue.status.model.이슈상태_데이터;
import com.arms.api.alm.issue.status.service.이슈상태_전략_호출;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/{connectId}/jira/issuestatus")
public class 이슈상태_컨트롤러 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 이슈상태_전략_호출 이슈상태_전략_호출;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<이슈상태_데이터> 온프레미스_이슈상태_목록_가져오기(@PathVariable("connectId") Long 연결_아이디) throws Exception {
        로그.info("온프레미스_이슈상태_목록_가져오기 :: 연결_아이디 :: {}", 연결_아이디);
        return 이슈상태_전략_호출.이슈상태_목록_가져오기(연결_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/project/{projectId}"},
            method = {RequestMethod.GET}
    )
    public List<이슈상태_데이터> 클라우드_프로젝트별_이슈상태_목록_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                                  @PathVariable("projectId") String 프로젝트_아이디) throws Exception {
        로그.info("클라우드_프로젝트별_이슈상태_목록_가져오기 :: 연결_아이디 :: {}, 프로젝트_키_또는_아이디 :: {}",
                                                                     연결_아이디, 프로젝트_아이디);
        return 이슈상태_전략_호출.프로젝트별_이슈상태_목록_가져오기(연결_아이디, 프로젝트_아이디);
    }
}
