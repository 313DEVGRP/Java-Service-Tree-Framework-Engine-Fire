package com.arms.api.alm.priority.strategy;

import com.arms.api.alm.priority.model.이슈우선순위_데이터;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class 이슈우선순위_전략_등록_및_실행 {

    이슈우선순위_전략 이슈우선순위_전략;

    public List<이슈우선순위_데이터> 우선순위_목록_가져오기(Long 연결_아이디) throws Exception {
        return this.이슈우선순위_전략.우선순위_목록_가져오기(연결_아이디);
    }

    public void 지라이슈우선순위_전략_등록(이슈우선순위_전략 이슈우선순위_전략) {
        this.이슈우선순위_전략 = 이슈우선순위_전략;
    }

}
