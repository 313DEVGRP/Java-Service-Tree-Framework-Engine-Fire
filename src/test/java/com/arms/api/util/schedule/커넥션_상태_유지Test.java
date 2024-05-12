package com.arms.api.util.schedule;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("dev")
@SpringBootTest
class 커넥션_상태_유지Test {

    @Autowired
    커넥션_상태_유지 커넥션_상태_유지;

    @Test
    public void test(){
        커넥션_상태_유지.커넥션_상태_유지();;
    }
}