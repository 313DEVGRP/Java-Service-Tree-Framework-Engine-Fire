package com.arms.api.alm.issue.base.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@SpringBootTest
class 서브테스크_조회Test {

    @Autowired
    private 서브테스크_조회 서브테스크_조회;

    @Test
    public void test(){
        서브테스크_조회.요구사항_링크드이슈_서브테스크_검색하기(344730167173788091L,"ARMS-176");
    }

}