package com.arms.api.engine.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class 검색어_날짜포함_검색_요청 extends 검색어_기본_검색_요청 {

    private String 시작_날짜;
    private String 끝_날짜;

    // 날짜 포멧에 따른 포메팅 따로 빼는것 검토
    private boolean 끝_날짜_확인(){
        return 끝_날짜()&&끝_날짜_길이_10();
    }

    private boolean 끝_날짜(){
        return this.끝_날짜!=null&&!this.끝_날짜.isBlank();
    }

    private boolean 끝_날짜_길이_10(){
        return this.시작_날짜.length()==10;
    }

    public boolean 시작_날짜_확인(){
        return 시작_날짜()&&시작_날짜_길이_10();
    }

    private boolean 시작_날짜(){
        return this.시작_날짜!=null&&!this.시작_날짜.isBlank();
    }

    private boolean 시작_날짜_길이_10(){
        return this.시작_날짜.length()==10;
    }

    public String 시작_날짜_포메팅(){
        if(시작_날짜_확인()){
            return this.시작_날짜+"T00:00:00.000";
        }
        return this.시작_날짜;
    }

    public String 끝_날짜_포메팅(){
        if(끝_날짜_확인()){
            return this.끝_날짜+"T00:00:00.000";
        }
        return this.끝_날짜;
    }


}
