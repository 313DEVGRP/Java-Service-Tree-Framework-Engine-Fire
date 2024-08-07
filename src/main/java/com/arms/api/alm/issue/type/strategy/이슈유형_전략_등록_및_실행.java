package com.arms.api.alm.issue.type.strategy;

import com.arms.api.alm.issue.type.model.이슈유형_데이터;
import com.arms.api.alm.serverinfo.model.서버정보_데이터;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class 이슈유형_전략_등록_및_실행 {

    이슈유형_전략 이슈유형_전략;

    public void 이슈유형_전략_등록(이슈유형_전략 이슈유형_전략) {
        this.이슈유형_전략 = 이슈유형_전략;
    }

    public List<이슈유형_데이터> 이슈유형_목록_가져오기(서버정보_데이터 서버정보) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        return this.이슈유형_전략.이슈유형_목록_가져오기(서버정보);
    }

    public List<이슈유형_데이터> 프로젝트별_이슈유형_목록_가져오기(서버정보_데이터 서버정보, String 프로젝트_아이디) throws URISyntaxException, IOException, ExecutionException, InterruptedException {
        return this.이슈유형_전략.프로젝트별_이슈유형_목록_가져오기(서버정보, 프로젝트_아이디);
    }


}
