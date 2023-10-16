package com.arms.elasticsearch.controllers;

import com.arms.elasticsearch.models.집계_응답;
import com.arms.elasticsearch.services.지라이슈_대시보드_서비스;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/engine/jira/dashboard")
@Slf4j
public class 엘라스틱_지라이슈_대시보드_컨트롤러 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private 지라이슈_대시보드_서비스 지라이슈_검색엔진;

    @ResponseBody
    @RequestMapping(
            value = {"/jira-issue-statuses"},
            method = {RequestMethod.GET}
    )
    public List<집계_응답> 요구사항이슈집계(
            @RequestParam Long pdServiceLink,
            @RequestParam List<Long> pdServiceVersionLinks
    ) throws IOException {
        List<집계_응답> 요구사항이슈집계 = 지라이슈_검색엔진.이슈상태집계(pdServiceLink, pdServiceVersionLinks);
        return 요구사항이슈집계;
    }

}
