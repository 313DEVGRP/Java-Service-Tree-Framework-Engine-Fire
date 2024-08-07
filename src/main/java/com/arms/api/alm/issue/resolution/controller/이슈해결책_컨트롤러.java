package com.arms.api.alm.issue.resolution.controller;

import com.arms.api.alm.issue.resolution.model.이슈해결책_데이터;
import com.arms.api.alm.issue.resolution.service.이슈해결책_전략_호출;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{connectId}/jira/issueresolution")
public class 이슈해결책_컨트롤러 {
    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    이슈해결책_전략_호출 이슈해결책_전략_호출;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<이슈해결책_데이터> 이슈해결책_목록_가져오기(@PathVariable("connectId") Long 연결_아이디) {
        로그.info("이슈해결책_목록_가져오기 :: 연결_아이디 :: {}", 연결_아이디);
        return 이슈해결책_전략_호출.이슈해결책_목록_가져오기(연결_아이디);
    }
}
