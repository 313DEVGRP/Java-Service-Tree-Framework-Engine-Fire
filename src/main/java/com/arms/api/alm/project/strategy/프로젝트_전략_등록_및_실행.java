package com.arms.api.alm.project.strategy;

import com.arms.api.alm.project.model.프로젝트_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;

import java.util.List;

public class 프로젝트_전략_등록_및_실행 {

    private 프로젝트_전략 프로젝트_전략;

    public void 프로젝트_전략_등록(프로젝트_전략 프로젝트_전략) {
        this.프로젝트_전략 = 프로젝트_전략;
    }

    public 프로젝트_데이터 프로젝트_상세정보_가져오기(서버정보_데이터 서버정보, String 프로젝트_키_또는_아이디) throws Exception {
        return this.프로젝트_전략.프로젝트_상세정보_가져오기(서버정보, 프로젝트_키_또는_아이디);
    }

    public List<프로젝트_데이터> 프로젝트_목록_가져오기(서버정보_데이터 서버정보) throws Exception {
        return this.프로젝트_전략.프로젝트_목록_가져오기(서버정보);
    }
}
