package com.arms.api.alm.project.strategy;

import com.arms.api.alm.project.model.프로젝트_데이터;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class 프로젝트_전략_등록_및_실행 {

    프로젝트_전략 프로젝트_전략;

    public 프로젝트_데이터 프로젝트_상세정보_가져오기(Long 연결_아이디, String 프로젝트_키_또는_아이디) throws Exception {
        return this.프로젝트_전략.프로젝트_상세정보_가져오기(연결_아이디, 프로젝트_키_또는_아이디);
    }

    public List<프로젝트_데이터> 프로젝트_목록_가져오기(Long 연결_아이디) throws Exception {
        return this.프로젝트_전략.프로젝트_목록_가져오기(연결_아이디);
    }

    public void 지라프로젝트_전략_등록(프로젝트_전략 프로젝트_전략) {
        this.프로젝트_전략 = 프로젝트_전략;
    }

}
