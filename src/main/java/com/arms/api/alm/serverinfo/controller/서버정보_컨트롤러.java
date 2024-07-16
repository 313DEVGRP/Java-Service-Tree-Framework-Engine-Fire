package com.arms.api.alm.serverinfo.controller;

import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_엔티티;
import com.arms.api.alm.serverinfo.service.서버정보_서비스;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/engine/serverinfo")
public class 서버정보_컨트롤러 {

    @Autowired
    private 서버정보_서비스 서버정보_서비스;

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @ResponseBody
    @RequestMapping(
            value = {""},
            method = {RequestMethod.POST}
    )
    public 서버정보_엔티티 서버정보_저장_또는_수정(@RequestBody 서버정보_데이터 서버정보_데이터,
                                  ModelMap model, HttpServletRequest request) throws Exception {

        로그.info("서버정보 저장");

        return 서버정보_서비스.서버정보_저장_또는_수정(서버정보_데이터);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/backup/scheduler"},
            method = {RequestMethod.POST}
    )
    @Async
    public CompletableFuture<Iterable<서버정보_엔티티>> 서버정보백업_스케줄러(ModelMap model, HttpServletRequest request) throws Exception {

        로그.info("서버정보백업 스케줄러");

        return 서버정보_서비스.서버정보백업_스케줄러();
    }

    /*
    * 삭제 관련 차후 설계 후 개발 진행
    * */
    @ResponseBody
    @RequestMapping(
            value = {""},
            method = {RequestMethod.DELETE}
    )
    public 서버정보_엔티티 서버정보_삭제(@RequestBody 서버정보_데이터 서버정보_데이터,
                            ModelMap model, HttpServletRequest request) throws Exception {

        로그.info("특정 서버정보 삭제");

        return 서버정보_서비스.서버정보_삭제하기(서버정보_데이터);
    }

    @ResponseBody
    @RequestMapping(
            value = {"/serverTypeMap"},
            method = {RequestMethod.GET}
    )
    public ResponseEntity<Map<String, String>> 서버_연결아이디_유형정보_맵_조회() {
        Map<String, String> 서버_연결아이디_유형_맵 = 서버정보_서비스.서버_연결아이디_유형_맵();
        return ResponseEntity.ok(서버_연결아이디_유형_맵);
    }
}
