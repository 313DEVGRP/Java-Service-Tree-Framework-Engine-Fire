package com.arms.jira.info.controller;

import com.arms.jira.info.model.지라연결정보_데이터;
import com.arms.jira.info.model.지라연결정보_엔티티;
import com.arms.jira.info.service.지라연결_서비스;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/jira")
public class 지라연결_컨트롤러 {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라연결_서비스 지라연결_서비스;

    @ResponseBody
    @RequestMapping(
            value = {"/connect/info"},
            method = {RequestMethod.POST}
    )
    public 지라연결정보_엔티티 setJiraConnectInfo(@RequestBody 지라연결정보_데이터 지라연결정보_데이터,
                                         ModelMap model, HttpServletRequest request) throws Exception {

        logger.info("Jira Connect Info SET API 호출");

        return 지라연결_서비스.saveConnectInfo(지라연결정보_데이터);
    }
}
