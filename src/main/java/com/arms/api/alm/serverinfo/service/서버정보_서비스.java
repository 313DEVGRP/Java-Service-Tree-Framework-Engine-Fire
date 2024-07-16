package com.arms.api.alm.serverinfo.service;

import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_엔티티;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface 서버정보_서비스 {

    public 서버정보_엔티티 서버정보_저장_또는_수정(서버정보_데이터 서버정보_데이터);

    public CompletableFuture<Iterable<서버정보_엔티티>> 서버정보백업_스케줄러();

    public 서버정보_엔티티 서버정보_삭제하기(서버정보_데이터 서버정보_데이터);

//    void 서버정보_전체_삭제하기();

    public 서버정보_데이터 서버정보_검증(Long 서버_아이디);

    Map<String, String> 서버_연결아이디_유형_맵();
}
