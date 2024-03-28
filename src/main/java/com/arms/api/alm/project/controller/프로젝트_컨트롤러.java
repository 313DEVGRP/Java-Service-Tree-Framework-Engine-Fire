package com.arms.api.alm.project.controller;

import com.arms.api.alm.project.model.프로젝트_데이터;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import com.arms.api.alm.project.service.프로젝트_전략_호출;


@RestController
@RequestMapping("/{connectId}/jira/project")
public class 프로젝트_컨트롤러 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    프로젝트_전략_호출 프로젝트_전략_호출;

    @ResponseBody
    @RequestMapping(
            value = {"/{projectKeyOrId}"},
            method = {RequestMethod.GET}
    )
    public 프로젝트_데이터 프로젝트_상세정보_가져오기(@PathVariable("projectKeyOrId") String 프로젝트_키_또는_아이디,
                                   @PathVariable("connectId") Long 연결_아이디,
                                   ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("지라 프로젝트 상세 정보 가져오기");

        return 프로젝트_전략_호출.프로젝트_상세정보_가져오기(연결_아이디, 프로젝트_키_또는_아이디);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<프로젝트_데이터> 프로젝트_목록_가져오기(@PathVariable("connectId") Long 연결_아이디,
                                       ModelMap model, HttpServletRequest request) throws Exception {
        로그.info("지라 프로젝트 전체 목록 가져오기");

        return 프로젝트_전략_호출.프로젝트_목록_가져오기(연결_아이디);
    }
}
