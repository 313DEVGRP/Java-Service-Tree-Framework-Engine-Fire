package com.arms.api.jira.jiraissuetype.strategy;

import com.arms.api.jira.jiraissuetype.model.지라이슈유형_데이터;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class 지라이슈유형_전략_등록_및_실행 {

    지라이슈유형_전략 지라이슈유형_전략;

    public List<지라이슈유형_데이터> 이슈유형_목록_가져오기(Long 연결_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        return this.지라이슈유형_전략.이슈유형_목록_가져오기(연결_아이디);
    }

    public List<지라이슈유형_데이터> 프로젝트별_이슈유형_목록_가져오기(Long 연결_아이디, String 프로젝트_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        return this.지라이슈유형_전략.프로젝트별_이슈유형_목록_가져오기(연결_아이디, 프로젝트_아이디);
    }

    public void 지라이슈유형_전략_등록(지라이슈유형_전략 지라이슈유형_전략) {
        this.지라이슈유형_전략 = 지라이슈유형_전략;
    }

}
