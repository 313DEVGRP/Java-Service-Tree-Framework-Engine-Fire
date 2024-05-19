package com.arms.api.alm.fluentd.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.arms.api.alm.fluentd.service.*;

@RestController
@RequestMapping("/engine/connection")
@Slf4j
public class 커넥션_상태_유지_컨트롤러 {

    @Autowired
    private 플루언트디_서비스 플루언트디_서비스;

    @GetMapping("/keep-alive")
    public void 커넥션_상태_유지() {
        log.info("[커넥션_상태_유지_컨트롤러 :: 커넥션_상태_유지]");
        플루언트디_서비스.커넥션_상태_유지();
    }

}
