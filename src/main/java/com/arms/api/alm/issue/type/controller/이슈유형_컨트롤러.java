package com.arms.api.alm.issue.type.controller;

import com.arms.api.alm.issue.type.model.이슈유형_데이터;
import com.arms.api.alm.issue.type.service.이슈유형_전략_호출;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/{connectId}/jira/issuetype")
public class 이슈유형_컨트롤러 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    이슈유형_전략_호출 이슈유형_전략_호출;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<이슈유형_데이터> 온프레미스_이슈유형_목록_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                             ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("지라 이슈유형_목록_가져오기");

        return 이슈유형_전략_호출.이슈유형_목록_가져오기(연결_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/project/{projectId}"},
            method = {RequestMethod.GET}
    )
    public List<이슈유형_데이터> 클라우드_프로젝트별_이슈유형_목록_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                                  @PathVariable("projectId") String 프로젝트_아이디,
                                                  ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("지라 프로젝트별_이슈유형_목록_가져오기");

        return 이슈유형_전략_호출.프로젝트별_이슈유형_목록_가져오기(연결_아이디, 프로젝트_아이디);
    }
}
