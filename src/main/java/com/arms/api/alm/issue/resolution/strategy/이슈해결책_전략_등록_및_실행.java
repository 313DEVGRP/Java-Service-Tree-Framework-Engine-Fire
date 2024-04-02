package com.arms.api.alm.issue.resolution.strategy;

import com.arms.api.alm.issue.resolution.model.이슈해결책_데이터;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class 이슈해결책_전략_등록_및_실행 {

    지라이슈해결책_전략 지라이슈해결책_전략;

    public List<이슈해결책_데이터> 이슈해결책_목록_가져오기(Long 연결_아이디) throws Exception {
        return this.지라이슈해결책_전략.이슈해결책_목록_가져오기(연결_아이디);
    }

    public void 지라이슈해결책_전략_등록(지라이슈해결책_전략 지라이슈해결책_전략) {
        this.지라이슈해결책_전략 = 지라이슈해결책_전략;
    }

}
