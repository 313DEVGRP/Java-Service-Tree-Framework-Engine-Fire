package com.arms.api.alm.issue.status.strategy;

import com.arms.api.alm.issue.status.model.이슈상태_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class 이슈상태_전략_등록_및_실행 {

    이슈상태_전략 이슈상태_전략;

    public void 이슈상태_전략_등록(이슈상태_전략 이슈상태_전략) {
        this.이슈상태_전략 = 이슈상태_전략;
    }

    public List<이슈상태_데이터> 이슈상태_목록_가져오기(서버정보_데이터 서버정보) throws Exception {
        return this.이슈상태_전략.이슈상태_목록_가져오기(서버정보);
    }

    public List<이슈상태_데이터> 프로젝트별_이슈상태_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_아이디) throws Exception {
        return this.이슈상태_전략.프로젝트별_이슈상태_목록_가져오기(서버정보, 프로젝트_아이디);
    }
}
