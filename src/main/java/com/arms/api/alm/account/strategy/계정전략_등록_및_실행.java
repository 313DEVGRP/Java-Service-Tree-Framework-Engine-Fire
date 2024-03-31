package com.arms.api.alm.account.strategy;

import com.arms.api.alm.account.model.계정정보_데이터;
import com.arms.api.serverinfo.model.서버정보_데이터;
import org.springframework.stereotype.Component;


@Component
public class 계정전략_등록_및_실행 {

    계정전략 계정전략;

    public void 계정_전략_등록(계정전략 계정전략) {
        this.계정전략 = 계정전략;
    }

    public 계정정보_데이터 계정정보_가져오기(Long 연결_아이디) throws Exception{
        return this.계정전략.계정정보_가져오기(연결_아이디);
    }

    public 계정정보_데이터 계정정보_검증(서버정보_데이터 서버정보) throws Exception{
        return this.계정전략.계정정보_검증(서버정보);
    }

}
