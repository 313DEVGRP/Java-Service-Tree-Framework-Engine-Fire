package com.arms.api.alm.issue.priority.strategy;

import com.arms.api.alm.issue.priority.model.이슈우선순위_데이터;

import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class 이슈우선순위_전략_등록_및_실행 {

    이슈우선순위_전략 이슈우선순위_전략;

    public void 이슈우선순위_전략_등록(이슈우선순위_전략 이슈우선순위_전략) {
        this.이슈우선순위_전략 = 이슈우선순위_전략;
    }

    public List<이슈우선순위_데이터> 우선순위_목록_가져오기(서버정보_데이터 서버정보) {
        return this.이슈우선순위_전략.우선순위_목록_가져오기(서버정보);
    }

}
