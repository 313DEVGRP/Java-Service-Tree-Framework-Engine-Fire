package com.arms.api.alm.issue.base.service;

import com.arms.api.util.model.dto.request.검색어_날짜포함_검색_요청;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class 지라이슈_검색_서비스_프로세스Test {

    @Autowired
    private 지라이슈_검색_서비스 지라이슈_검색_서비스;

    @Test
    public void test(){
        검색어_날짜포함_검색_요청 검색어_날짜포함_검색_요청 = new 검색어_날짜포함_검색_요청();
        검색어_날짜포함_검색_요청.set페이지_처리_여부(false);
        검색어_날짜포함_검색_요청.set크기(100);
        검색어_날짜포함_검색_요청.set페이지(0);
        검색어_날짜포함_검색_요청.set검색어("YHS");
        지라이슈_검색_서비스.지라이슈_날짜포함_검색(검색어_날짜포함_검색_요청);
    }

}