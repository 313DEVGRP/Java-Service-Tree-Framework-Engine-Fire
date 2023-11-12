package com.arms.api.serverinfo.helper;

public enum 서버유형_정보 {

    클라우드("클라우드"), 온프레미스("온프레미스");

    private final String 유형;

    서버유형_정보(String 유형) {
        this.유형 = 유형;
    }
}
