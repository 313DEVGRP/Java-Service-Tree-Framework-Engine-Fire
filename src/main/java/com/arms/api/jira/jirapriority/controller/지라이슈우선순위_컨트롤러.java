package com.arms.api.jira.jirapriority.controller;

import com.arms.api.jira.jirapriority.model.지라이슈우선순위_데이터;
import com.arms.api.jira.jirapriority.service.지라이슈우선순위_전략_호출;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{connectId}/jira/issuepriority")
public class 지라이슈우선순위_컨트롤러 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    지라이슈우선순위_전략_호출 지라이슈우선순위_전략_호출;

    @ResponseBody
    @RequestMapping(
            value = {"/list"},
            method = {RequestMethod.GET}
    )
    public List<지라이슈우선순위_데이터> 우선순위_목록_가져오기(@PathVariable("connectId") Long 연결_아이디) throws Exception {
        로그.info("지라 이슈 우선순위 전체 목록 가져오기");
        return 지라이슈우선순위_전략_호출.우선순위_목록_가져오기(연결_아이디);
    }

}
