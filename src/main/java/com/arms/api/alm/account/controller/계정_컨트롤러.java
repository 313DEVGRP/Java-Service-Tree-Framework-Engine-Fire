package com.arms.api.alm.account.controller;

import com.arms.api.alm.account.model.계정정보_데이터;
import com.arms.api.alm.account.service.계정전략_호출;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alm/account")
public class 계정_컨트롤러 {

    private final Logger 로그 = LoggerFactory.getLogger(this.getClass());

    @Autowired
    계정전략_호출 계정전략_호출;

    /*
    *  서버 등록시에 계정 정보
    * */
    @ResponseBody
    @RequestMapping(
            value = {"/verify"},
            method = {RequestMethod.GET}
    )
    public 계정정보_데이터 계정정보_검증하기(
            @RequestBody 서버정보_데이터 서버정보_데이터
            )throws Exception{


        로그.info("계정_컨트롤러 :: 계정정보_검증하기, 서버 정보 데이터: {}",서버정보_데이터);

        return 계정전략_호출.계정정보_검증하기(서버정보_데이터);
    }

    /*
    *  계정 정보가 es에 저장된 경우 조회
    * */
    @ResponseBody
    @RequestMapping(
            value = {"/{connectId}/myself"},
            method = {RequestMethod.GET}
    ) // 임시 설정
    public 계정정보_데이터 계정정보_가져오기(@PathVariable("connectId") Long 연결_아이디)throws Exception{

        로그.info("계정_컨트롤러 :: 계정정보_가져오기, 연결_아이디: {}",연결_아이디);

        return 계정전략_호출.계정정보_가져오기(연결_아이디);
    }

//    @ResponseBody
//    @RequestMapping(
//            value = {"/permissions"},
//            method = {RequestMethod.GET}
//    )// 임시 설정
//    public void 나의_계정권한_가져오기(@PathVariable("connectId") Long 연결_아이디)throws Exception{
//        로그.info("계정_컨트롤러 :: 나의_계정권한_가져오기");
//
//        //return 계정전략_호출.계정권한_가져오기(연결_아이디);
//    }


}
