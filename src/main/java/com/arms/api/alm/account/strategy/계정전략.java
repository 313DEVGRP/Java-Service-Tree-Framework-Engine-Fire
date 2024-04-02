package com.arms.api.alm.account.strategy;

import com.arms.api.alm.account.model.계정정보_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;

public interface 계정전략 {

    계정정보_데이터 계정정보_가져오기(Long 연결_아이디) throws Exception;

    계정정보_데이터 계정정보_검증(서버정보_데이터 서버정보) throws Exception;
}
