package com.arms.api.engine.controller;

import com.arms.api.engine.service.지라이슈_대시보드_서비스;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/engine/analysis")
@Slf4j
public class 엔진_분석메뉴_컨트롤러 {

    @Autowired
    private 지라이슈_대시보드_서비스 지라이슈_검색엔진;
}
